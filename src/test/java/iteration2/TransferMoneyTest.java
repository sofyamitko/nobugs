package iteration2;

import asserts.comparison.ModelAssertions;
import iteration1.BaseTest;
import models.accounts.AccountResponseModel;
import models.accounts.TransactionResponseModel;
import models.accounts.TransferMoneyRequestModel;
import models.accounts.TransferMoneyResponseModel;
import models.admin.CreateUserRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import asserts.AccountBalanceSnapshot;
import asserts.TransactionAssert;

import java.util.stream.Stream;


public class TransferMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountBetweenOwnAccounts(double amount) {

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        //увеличение баланса первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(amount)
                .build();

        TransferMoneyResponseModel transferMoneyResponse = new ValidatedCrudRequester<TransferMoneyResponseModel>(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOkSpec())
                .post(transferMoneyRequest);

        //проверка тела ответа
        ModelAssertions.assertThatModels(transferMoneyRequest, transferMoneyResponse).match();

        //проверка наличия транзакций по переводу на аккаунтах
        TransactionResponseModel transactionFirstAccount = UserSteps.getTransaction(user.getUsername(), user.getPassword(), account1.getId(), amount, account2.getId());
        TransactionAssert.assertThat(transactionFirstAccount).isTransferOut(amount, account2.getId());

        TransactionResponseModel transactionSecondAccount = UserSteps.getTransaction(user.getUsername(), user.getPassword(), account2.getId(), amount, account1.getId());
        TransactionAssert.assertThat(transactionSecondAccount).isTransferIn(amount, account1.getId());

        //проверка изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isDecreasedBy(amount);
        balanceReceiverAccount.assertThat().isIncreasedBy(amount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountToAnotherUsersAccount(double amount) {

        CreateUserRequestModel user1 = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user1);
        CreateUserRequestModel user2 = AdminSteps.createUser();
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        //увеличение баланса первого аккаунта
        UserSteps.depositAccount(user1.getUsername(), user1.getPassword(), account1.getId(), 15000);

        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(amount)
                .build();

        TransferMoneyResponseModel transferMoneyResponse = new ValidatedCrudRequester<TransferMoneyResponseModel>(RequestSpecs.authAsUserSpec(
                user1.getUsername(), user1.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOkSpec())
                .post(transferMoneyRequest);

        //проверка тела ответа
        ModelAssertions.assertThatModels(transferMoneyRequest, transferMoneyResponse).match();

        //проверка наличия транзакций по переводу на аккаунтах
        TransactionResponseModel transactionFirstAccount = UserSteps.getTransaction(user1.getUsername(), user1.getPassword(), account1.getId(), amount, account2.getId());
        TransactionAssert.assertThat(transactionFirstAccount).isTransferOut(amount, account2.getId());

        TransactionResponseModel transactionSecondAccount = UserSteps.getTransaction(user2.getUsername(), user2.getPassword(), account2.getId(), amount, account1.getId());
        TransactionAssert.assertThat(transactionSecondAccount).isTransferIn(amount, account1.getId());

        //проверка изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isDecreasedBy(amount);
        balanceReceiverAccount.assertThat().isIncreasedBy(amount);
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

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        //увеличение баланса первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(amount)
                .build();

        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferMoneyRequest);

        //проверка отсутствия изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
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

        CreateUserRequestModel user1 = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user1);
        CreateUserRequestModel user2 = AdminSteps.createUser();
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        //увеличение баланса первого аккаунта
        UserSteps.depositAccount(user1.getUsername(), user1.getPassword(), account1.getId(), 15000);

        //создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(amount)
                .build();

        new CrudRequester(RequestSpecs.authAsUserSpec(
                user1.getUsername(), user1.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferMoneyRequest);

        //проверка отсутствия изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceBetweenOwnAccounts() {

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        //предварительное пополнение баланса отсутствует
        //создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(1.0)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);

        //проверка отсутствия изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceToAnotherUsersAccount() {

        CreateUserRequestModel user1 = AdminSteps.createUser();
        AccountResponseModel account1 = UserSteps.createAccount(user1);
        CreateUserRequestModel user2 = AdminSteps.createUser();
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        //предварительное пополнение баланса отсутствует
        //создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        TransferMoneyRequestModel transferMoneyRequest = TransferMoneyRequestModel
                .builder()
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .amount(0.1)
                .build();

        new CrudRequester(RequestSpecs.authAsUserSpec(
                user1.getUsername(), user1.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);

        //проверка отсутствия изменения состояния баланса по аккаунтам
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }
}