package ui.iteration2;

import api.asserts.AccountBalanceSnapshot;
import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.iteration1.BaseUITest;
import ui.pages.BankAlert;
import ui.pages.DepositPage;

public class DepositMoneyTest extends BaseUITest {

    @Test
    public void userCanIncreaseDepositTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user.getUsername(), user.getPassword());

        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).enterMoney(100.50).submitDeposit();

        String expectedAlert = BankAlert.SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.format(100.50, account.getAccountNumber());
        new DepositPage().checkAlertMessageAndAccept(expectedAlert);

        //проверка успешного измененения состояния баланса
        balance.assertThat().isIncreasedBy(100.50);
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненным аккаунтом
    @Test
    public void userCanNotIncreaseDepositWithEmptyAccountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user.getUsername(), user.getPassword());

        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().enterMoney(100.50).submitDeposit().checkAlertMessageAndAccept(BankAlert.SELECT_ACCOUNT.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненной суммой
    @Test
    public void userCanNotIncreaseDepositWithEmptyAmountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user.getUsername(), user.getPassword());

        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).submitDeposit()
                .checkAlertMessageAndAccept(BankAlert.ENTER_VALID_AMOUNT.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }

    // Проверка валидации c API (интеграция) - ошибка при пополнении депозита с невалидной суммой
    @Test
    public void userCanNotIncreaseDepositWithInvalidAmountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user.getUsername(), user.getPassword());

        AccountResponseModel account = UserSteps.createAccount(user);

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).enterMoney(5000.01)
                .submitDeposit()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }
}