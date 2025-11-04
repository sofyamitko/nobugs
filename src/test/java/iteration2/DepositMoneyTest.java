package iteration2;

import iteration1.BaseTest;
import models.accounts.AccountRequestModel;
import models.accounts.AccountResponseModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.DepositAccountRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.AssertionsUtils;
import utils.factory.TestUserContext;

import java.util.stream.Stream;


public class DepositMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanIncreaseDepositOfExistingAccount(double amount) {

        TestUserContext testUser = factory.createUserWithAccounts();

        // получение баланса аккаунта до депозита
        double balanceBeforeDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(testUser.getFirstAccountId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        AccountResponseModel accountRequestModelAfterDeposit = new DepositAccountRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsOkSpec())
                .post(accountRequest)
                .extract().as(AccountResponseModel.class);

        //проверка тела ответа
        AssertionsUtils.assertSuccessfulDeposit(softly, accountRequestModelAfterDeposit, amount);

        // получение баланса после депозита
        double balanceAfterDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        //проверка изменения состояния баланса
        AssertionsUtils.assertBalancesUpdatedAfterDeposit(softly, balanceBeforeDeposit, balanceAfterDeposit, amount);
    }

    public static Stream<Arguments> validAmountForSeveralDeposit() {

        return Stream.of(
                Arguments.of(0.01, 2),
                Arguments.of(5000, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("validAmountForSeveralDeposit")
    public void userCanIncreaseDepositOfExistingAccountSeveralTimes(double amount, int times) {

        TestUserContext testUser = factory.createUserWithAccounts();

        // получение баланса аккаунта до депозита
        double balanceBeforeDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(testUser.getFirstAccountId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        for (int i = 0; i < times; i++) {
            new DepositAccountRequester(
                    RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                    ResponseSpecs.requestReturnsOkSpec())
                    .post(accountRequest);
        }

        // получение баланса после депозита
        double balanceAfterDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        //проверка изменения состояния баланса
        AssertionsUtils.assertBalancesUpdatedAfterDepositSeveralTimes(softly, balanceBeforeDeposit, balanceAfterDeposit, amount, times);
    }

    public static Stream<Arguments> invalidAmount() {

        return Stream.of(
                Arguments.of(0.0, "Deposit amount must be at least 0.01"),
                Arguments.of(-2, "Deposit amount must be at least 0.01"),
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCanNotIncreaseDepositOfExistingAccountByInvalidAmount(double amount, String errorMessage) {

        TestUserContext testUser = factory.createUserWithAccounts();

        // получение баланса аккаунта до депозита
        double balanceBeforeDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(testUser.getFirstAccountId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new DepositAccountRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(accountRequest);

        // получение баланса после депозита
        double balanceAfterDeposit = accountBalanceUtils.getBalanceOfAccount(testUser.getUsername(), testUser.getPassword(), testUser.getFirstAccountId());

        //проверка отсутствия измененения состояния баланса
        AssertionsUtils.assertBalancesUnchangedAfterDeposit(softly, balanceBeforeDeposit, balanceAfterDeposit);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanNotIncreaseDepositOfAnotherAccountByValidAmount(double amount) {

        TestUserContext testUser1 = factory.createUserWithAccount();
        TestUserContext testUser2 = factory.createUserWithAccount();

        // получение баланса аккаунта до депозита
        double balance2BeforeDeposit = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(), testUser2.getPassword(), testUser2.getFirstAccountId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(testUser2.getFirstAccountId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new DepositAccountRequester(
                RequestSpecs.authAsUserSpec(testUser1.getUsername(), testUser1.getPassword()),
                ResponseSpecs.requestForbiddenSpec("Unauthorized access to account"))
                .post(accountRequest);

        // получение баланса после депозита
        double balance2AfterDeposit = accountBalanceUtils.getBalanceOfAccount(testUser2.getUsername(), testUser2.getPassword(), testUser2.getFirstAccountId());

        //проверка отсутствия измененения состояния баланса
        AssertionsUtils.assertBalancesUnchangedAfterDeposit(softly, balance2BeforeDeposit, balance2AfterDeposit);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanNotIncreaseDepositOfNotExistingAccountByValidAmount(double amount) {

        TestUserContext testUser = factory.createUserWithAccount();

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(0) //аккаунта с id = 0 не существует
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new DepositAccountRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestForbiddenSpec("Unauthorized access to account"))
                .post(accountRequest);
    }
}