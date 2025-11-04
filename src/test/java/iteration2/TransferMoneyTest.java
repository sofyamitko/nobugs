package iteration2;

import iteration1.BaseTest;
import models.accounts.AccountTransactionModel;
import models.accounts.TransferMoneyRequestModel;
import models.accounts.TransferMoneyResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.TransferMoneyRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.AssertionsUtils;
import utils.factory.TestUserContext;

import java.util.stream.Stream;


public class TransferMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountBetweenOwnAccounts(double amount) {

        TestUserContext testUser = factory.createUserWithAccounts();

        //увеличение баланса первого аккаунта
        accountBalanceUtils.depositAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId(), 15000);

        //получение баланса по аккаунтам до перевода
        double balanceFirstAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());
        double balanceSecondAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getSecondAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser.getFirstAccountId())
                .receiverAccountId(testUser.getSecondAccountId())
                .amount(amount)
                .build();

        TransferMoneyResponseModel transferMoneyResponse = new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsOkSpec())
                .post(transferMoneyRequest)
                .extract()
                .as(TransferMoneyResponseModel.class);

        //проверка тела ответа
        AssertionsUtils.assertSuccessfulTransfer(softly, transferMoneyResponse, amount, testUser.getFirstAccountId(), testUser.getSecondAccountId());

        //проверка наличия транзакций по переводу на аккаунтах
        AccountTransactionModel transactionFirstAccount = accountBalanceUtils.getTransactionsOfIdAccount(testUser.getUsername(), testUser.getPassword(),
                testUser.getFirstAccountId(), amount, testUser.getSecondAccountId());
        AssertionsUtils.assertTransaction(softly, transactionFirstAccount,"TRANSFER_OUT");

        AccountTransactionModel transactionSecondAccount = accountBalanceUtils.getTransactionsOfIdAccount(testUser.getUsername(), testUser.getPassword(),
                testUser.getSecondAccountId(), amount, testUser.getFirstAccountId());
        AssertionsUtils.assertTransaction(softly, transactionSecondAccount,"TRANSFER_IN");

        //получение баланса по аккаунтам после перевода
        double balanceFirstAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(),
                testUser.getFirstAccountId());
        double balanceSecondAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(),
                testUser.getSecondAccountId());

        //проверка изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUpdatedAfterTransfer(softly, balanceFirstAccountBeforeTransfer,
                balanceSecondAccountBeforeTransfer, balanceFirstAccountAfterTransfer, balanceSecondAccountAfterTransfer, amount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountToAnotherUsersAccount(double amount) {

        TestUserContext testUser1 = factory.createUserWithAccounts();
        TestUserContext testUser2 = factory.createUserWithAccounts();

        //увеличение баланса аккаунта первого юзера
        accountBalanceUtils.depositAccount(testUser1.getUsername(), testUser1.getPassword(), testUser1.getFirstAccountId(), 15000);

        //получение баланса по аккаунтам до перевода
        double balanceAccountOfFirstUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(), testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(), testUser2.getPassword(), testUser2.getFirstAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser1.getFirstAccountId())
                .receiverAccountId(testUser2.getFirstAccountId())
                .amount(amount)
                .build();

        TransferMoneyResponseModel transferMoneyResponse = new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser1.getUsername(), testUser1.getPassword()),
                ResponseSpecs.requestReturnsOkSpec())
                .post(transferMoneyRequest)
                .extract()
                .as(TransferMoneyResponseModel.class);

        //проверка тела ответа
        AssertionsUtils.assertSuccessfulTransfer(softly, transferMoneyResponse, amount, testUser1.getFirstAccountId(), testUser2.getFirstAccountId());

        //проверка наличия транзакций по переводу на аккаунтах
        AccountTransactionModel transactionFirstAccount = accountBalanceUtils.getTransactionsOfIdAccount(testUser1.getUsername(),
                testUser1.getPassword(), testUser1.getFirstAccountId(), amount, testUser2.getFirstAccountId());
        AssertionsUtils.assertTransaction(softly, transactionFirstAccount,"TRANSFER_OUT");

        AccountTransactionModel transactionSecondAccount = accountBalanceUtils.getTransactionsOfIdAccount(testUser2.getUsername(),
                testUser2.getPassword(), testUser2.getFirstAccountId(), amount, testUser1.getFirstAccountId());
        AssertionsUtils.assertTransaction(softly, transactionSecondAccount,"TRANSFER_IN");

        //получение баланса по аккаунтам после перевода
        double balanceAccountOfFirstUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(),
                testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(),
                testUser2.getPassword(), testUser2.getFirstAccountId());

        //проверка изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUpdatedAfterTransfer(softly, balanceAccountOfFirstUserBeforeTransfer,
                balanceAccountOfSecondUserBeforeTransfer, balanceAccountOfFirstUserAfterTransfer, balanceAccountOfSecondUserAfterTransfer, amount);
    }

    public static Stream<Arguments> invalidAmountForTransfer() {
        return Stream.of(
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(-1, "Transfer amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmountForTransfer")
    public void userCanNotTransferInvalidAmountBetweenOwnAccounts(double amount, String errorMessage) {

        TestUserContext testUser = factory.createUserWithAccounts();

        //увеличение баланса первого аккаунта
        accountBalanceUtils.depositAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId(), 15000);

        //получение баланса по аккаунтам до перевода
        double balanceFirstAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());
        double balanceSecondAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getSecondAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser.getFirstAccountId())
                .receiverAccountId(testUser.getSecondAccountId())
                .amount(amount)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferMoneyRequest);

        //получение баланса по аккаунтам после перевода
        double balanceFirstAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(),
                testUser.getPassword(), testUser.getFirstAccountId());
        double balanceSecondAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(),
                testUser.getPassword(), testUser.getSecondAccountId());

        //проверка отсутствия изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUnchangedAfterTransfer(softly, balanceFirstAccountBeforeTransfer, balanceSecondAccountBeforeTransfer,
                balanceFirstAccountAfterTransfer, balanceSecondAccountAfterTransfer);
    }

    public static Stream<Arguments> invalidAmountForTransferToAnotherAccount() {
        return Stream.of(
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(-1, "Transfer amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmountForTransferToAnotherAccount")
    public void userCanNotTransferInvalidAmountToAnotherUsersAccount(double amount, String errorMessage) {

        TestUserContext testUser1 = factory.createUserWithAccounts();
        TestUserContext testUser2 = factory.createUserWithAccounts();

        //увеличение баланса аккаунта первого юзера
        accountBalanceUtils.depositAccount(testUser1.getUsername(), testUser1.getPassword(), testUser1.getFirstAccountId(), 5000);

        //получение баланса по аккаунтам до перевода
        double balanceAccountOfFirstUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(), testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(), testUser2.getPassword(), testUser2.getFirstAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser1.getFirstAccountId())
                .receiverAccountId(testUser2.getFirstAccountId())
                .amount(amount)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser1.getUsername(), testUser1.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferMoneyRequest);

        //получение баланса по аккаунтам после перевода
        double balanceAccountOfFirstUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(),
                testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(),
                testUser2.getPassword(), testUser2.getFirstAccountId());

        //проверка отсутствия изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUnchangedAfterTransfer(softly, balanceAccountOfFirstUserBeforeTransfer, balanceAccountOfSecondUserBeforeTransfer,
                balanceAccountOfFirstUserAfterTransfer, balanceAccountOfSecondUserAfterTransfer);
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceBetweenOwnAccounts() {

        TestUserContext testUser = factory.createUserWithAccounts();

        //получение баланса по аккаунтам до перевода
        //предварительное пополнение баланса отсутствует
        double balanceFirstAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());
        double balanceSecondAccountBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getSecondAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser.getFirstAccountId())
                .receiverAccountId(testUser.getSecondAccountId())
                .amount(1.0)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);

        //получение баланса по аккаунтам после перевода
        double balanceFirstAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(),
                testUser.getPassword(), testUser.getFirstAccountId());
        double balanceSecondAccountAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(),
                testUser.getPassword(), testUser.getSecondAccountId());

        //проверка отсутствия изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUnchangedAfterTransfer(softly, balanceFirstAccountBeforeTransfer, balanceSecondAccountBeforeTransfer,
                balanceFirstAccountAfterTransfer, balanceSecondAccountAfterTransfer);
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceToAnotherUsersAccount() {

        TestUserContext testUser1 = factory.createUserWithAccounts();
        TestUserContext testUser2 = factory.createUserWithAccounts();

        //получение баланса по аккаунтам до перевода
        //предварительное пополнение баланса отсутствует
        double balanceAccountOfFirstUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(), testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserBeforeTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(), testUser2.getPassword(), testUser2.getFirstAccountId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(testUser1.getFirstAccountId())
                .receiverAccountId(testUser2.getFirstAccountId())
                .amount(1.0)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUserSpec(testUser1.getUsername(), testUser1.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);

        //получение баланса по аккаунтам после перевода
        double balanceAccountOfFirstUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser1.getUsername(),
                testUser1.getPassword(), testUser1.getFirstAccountId());
        double balanceAccountOfSecondUserAfterTransfer = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(),
                testUser2.getPassword(), testUser2.getFirstAccountId());

        //проверка отсутствия изменения состояния баланса по аккаунтам
        AssertionsUtils.assertBalancesUnchangedAfterTransfer(softly, balanceAccountOfFirstUserBeforeTransfer, balanceAccountOfSecondUserBeforeTransfer,
                balanceAccountOfFirstUserAfterTransfer, balanceAccountOfSecondUserAfterTransfer);
    }
}