package ui.iteration2;

import api.asserts.AccountBalanceSnapshot;
import api.generators.RandomData;
import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import common.annotations.UserSessionWithAccounts;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ui.iteration1.BaseUITest;
import ui.pages.BankAlert;
import ui.pages.DepositPage;

import java.util.List;

public class DepositMoneyTest extends BaseUITest {

    @Test
    @UserSessionWithAccounts
    public void userCanIncreaseDepositTest() {
        // генерация валидного значения amount для депозита
        double amount = RandomData.getAmount(0.1, 5000);

        CreateUserRequestModel user = SessionStorage.getUser();

        List<AccountResponseModel> accounts = SessionStorage.getSteps().getAllAccounts();
        AccountResponseModel account = accounts.getFirst();

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).enterMoney(amount).submitDeposit();

        String expectedAlert = BankAlert.SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.format(amount, account.getAccountNumber());
        new DepositPage().checkAlertMessageAndAccept(expectedAlert);

        //проверка успешного измененения состояния баланса
        balance.assertThat().isIncreasedBy(amount);
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненным аккаунтом
    @Test
    @UserSessionWithAccounts
    public void userCanNotIncreaseDepositWithEmptyAccountTest() {
        // генерация валидного значения amount для депозита
        double amount = RandomData.getAmount(0.1, 5000);

        CreateUserRequestModel user = SessionStorage.getUser();

        List<AccountResponseModel> accounts = SessionStorage.getSteps().getAllAccounts();
        AccountResponseModel account = accounts.getFirst();

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().enterMoney(amount).submitDeposit().checkAlertMessageAndAccept(BankAlert.SELECT_ACCOUNT.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненной суммой
    @Test
    @UserSessionWithAccounts
    public void userCanNotIncreaseDepositWithEmptyAmountTest() {
        CreateUserRequestModel user = SessionStorage.getUser();

        List<AccountResponseModel> accounts = SessionStorage.getSteps().getAllAccounts();
        AccountResponseModel account = accounts.getFirst();

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).submitDeposit()
                .checkAlertMessageAndAccept(BankAlert.ENTER_VALID_AMOUNT.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }

    // Проверка валидации c API (интеграция) - ошибка при пополнении депозита с невалидной суммой
    @ParameterizedTest
    @ValueSource(doubles = {5000.01})
    @UserSessionWithAccounts
    public void userCanNotIncreaseDepositWithInvalidAmountTest(double amount) {
        CreateUserRequestModel user = SessionStorage.getUser();

        List<AccountResponseModel> accounts = SessionStorage.getSteps().getAllAccounts();
        AccountResponseModel account = accounts.getFirst();

        // создание снэпшота текущего состояния баланса (до выполнения депозита)
        AccountBalanceSnapshot balance = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account.getId());

        new DepositPage().open().selectAccount(account.getAccountNumber()).enterMoney(amount)
                .submitDeposit()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        //проверка отсутствия измененения состояния баланса
        balance.assertThat().isUnchanged();
    }
}