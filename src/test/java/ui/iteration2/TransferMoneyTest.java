package ui.iteration2;

import api.asserts.AccountBalanceSnapshot;
import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.iteration1.BaseUITest;
import ui.pages.BankAlert;
import ui.pages.TransferPage;

public class TransferMoneyTest extends BaseUITest {

    @Test
    public void userCanTransferAmountBetweenOwnAccountsTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        String expectedAlert = BankAlert.SUCCESSFULLY_TRANSFERRED.format(100.00, account2.getAccountNumber());
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterRecipientName("Noname")
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(100.00)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(expectedAlert)
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка изменения баланса по API
        balanceSenderAccount.assertThat().isDecreasedBy(100.00);
        balanceReceiverAccount.assertThat().isIncreasedBy(100.00);
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceBetweenOwnAccountsTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 1000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(2000.00)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    @Test
    public void userCanTransferAmountWithEmptyRecipientNameBetweenOwnAccountsTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        String expectedAlert = BankAlert.SUCCESSFULLY_TRANSFERRED.format(100.00, account2.getAccountNumber());
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(100.00)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(expectedAlert)
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка изменения баланса по API
        balanceSenderAccount.assertThat().isDecreasedBy(100.00);
        balanceReceiverAccount.assertThat().isIncreasedBy(100.00);
    }

    //Негативная проверка по переводу невалидной суммы на другой аккаунт
    @Test
    public void userCanNotTransferInvalidAmountBetweenOwnAccountsTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(0)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу суммы c пустым отправителем
    @Test
    public void userCanNotTransferWithEmptySenderAccountTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open()
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(50)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }


    //Негативная проверка по переводу суммы c пустым получателем
    @Test
    public void userCanNotTransferWithEmptyRecipientAccountTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterAmount(50)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу с пустой суммой
    @Test
    public void userCanNotTransferWithEmptyAmountTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open()
                .selectSenderAccount(account1.getAccountNumber())
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу с неотмеченным чекбоксом
    @Test
    public void userCanNotTransferWithEmptyConfirmationCheckboxTest() {
        //подготовка данных
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //шаги теста
        new TransferPage().open()
                .selectSenderAccount(account1.getAccountNumber())
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(50)
                .pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage())
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter())
                .checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка отсутствия изменения баланса по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    @Test
    public void userCanTransferValidAmountToAnotherUsersAccountTest() {
        //подготовка данных
        CreateUserRequestModel user1 = AdminSteps.createUser();
        CreateUserRequestModel user2 = AdminSteps.createUser();

        authAsUser(user1);

        AccountResponseModel account1 = UserSteps.createAccount(user1);
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        UserSteps.depositAccount(user1.getUsername(), user1.getPassword(), account1.getId(), 15000);

        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        //шаги теста
        String expectedAlert = BankAlert.SUCCESSFULLY_TRANSFERRED.format(100.00, account2.getAccountNumber());
        new TransferPage().open().selectSenderAccount(account1.getAccountNumber())
                .enterRecipientName("Noname")
                .enterRecipientAccountNumber(account2.getAccountNumber())
                .enterAmount(100.00)
                .checkConfirmCheckbox()
                .pressTransferButton()
                .checkAlertMessageAndAccept(expectedAlert)
                .checkBalanceAccount(account1.getAccountNumber(), balanceSenderAccount.getAfter());

        logout();
        authAsUser(user2);
        new TransferPage().open().checkBalanceAccount(account2.getAccountNumber(), balanceReceiverAccount.getAfter());

        // проверка изменения баланса по API
        balanceSenderAccount.assertThat().isDecreasedBy(100.00);
        balanceReceiverAccount.assertThat().isIncreasedBy(100.00);
    }
}