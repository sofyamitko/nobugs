package ui.iteration1;

import api.asserts.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.elements.UserBage;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends BaseUITest {

    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);

        assertTrue(new AdminPanel().open()
                .createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers().stream().anyMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));


        CreateUserResponseModel createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }


    @Test
    @AdminSession
    public void adminCanNotCreateUserWithInvalidDataTest() {
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        newUser.setUsername("a");

        assertTrue(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        long usersWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).count();

        assertThat(usersWithSameUsernameAsNewUser).isZero();
    }
}
