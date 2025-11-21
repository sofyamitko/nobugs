package api.iteration2;

import api.asserts.comparison.ModelAssertions;
import api.iteration1.BaseTest;
import api.models.accounts.AccountRequestModel;
import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.asserts.AccountBalanceSnapshot;

import java.util.stream.Stream;


public class DepositMoneyTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanIncreaseDepositOfExistingAccount(double amount) {

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        AccountResponseModel accountResponseModelAfterDeposit = new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOkSpec())
                .post(accountRequest);

        //проверка тела ответа
        ModelAssertions.assertThatModels(accountRequest, accountResponseModelAfterDeposit).match();

        //проверка изменения состояния баланса
        balance.assertThat().isIncreasedBy(amount);
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

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        for (int i = 0; i < times; i++) {
            new ValidatedCrudRequester<AccountResponseModel>(
                    RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOkSpec())
                    .post(accountRequest);
        }

        //проверка изменения состояния баланса
        balance.assertThat().isIncreasedSeveralTimesBy(amount, times);
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

        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new CrudRequester(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(accountRequest);

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanNotIncreaseDepositOfAnotherAccountByValidAmount(double amount) {

        CreateUserRequestModel user1 = AdminSteps.createUser();
        CreateUserRequestModel user2 = AdminSteps.createUser();
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(account2.getId())
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new CrudRequester(
                RequestSpecs.authAsUserSpec(user1.getUsername(), user1.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestForbiddenSpec("Unauthorized access to account"))
                .post(accountRequest);

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanNotIncreaseDepositOfNotExistingAccountByValidAmount(double amount) {

        CreateUserRequestModel user = AdminSteps.createUser();

        AccountRequestModel accountRequest = AccountRequestModel.builder()
                .id(0) //аккаунта с id = 0 не существует
                .balance(amount)
                .build();

        // пополнение аккаунта на сумму депозита
        new CrudRequester(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestForbiddenSpec("Unauthorized access to account"))
                .post(accountRequest);
    }
}