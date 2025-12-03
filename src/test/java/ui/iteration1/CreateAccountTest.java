package ui.iteration1;

import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUITest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        new UserDashboard().open().createNewAccount();

        List<AccountResponseModel> createdAccounts = SessionStorage.getSteps().getAllAccounts();

        assertThat(createdAccounts).hasSize(1);

        String expectedAlert = BankAlert.NEW_ACCOUNT_CREATED.format(createdAccounts.getFirst().getAccountNumber());
        new UserDashboard().checkAlertMessageAndAccept(expectedAlert);

        assertThat(createdAccounts.getFirst().getBalance()).isZero();
    }
}
