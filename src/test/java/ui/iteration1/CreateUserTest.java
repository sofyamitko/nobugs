package ui.iteration1;

import api.asserts.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseUITest {

    @Test
    public void adminCanCreateUserTest() {
        //шаг 1: админ залогинился
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        authAsUser(admin);

        //шаг 2: админ создает юзера
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);

        new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER"))
                .shouldBe(Condition.visible);

        //шаг 5: проверка, что юзер создан на API

        CreateUserResponseModel createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst()
                .get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }


    @Test
    public void adminCanNotCreateUserWithInvalidDataTest() {
        //шаг 1: админ залогинился
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        authAsUser(admin);

        //шаг 2: админ создает юзера
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        newUser.setUsername("a");

        new AdminPanel().open()
                .createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers()
                .findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

        //шаг 5: проверка, что юзер НЕ создан на API

        long usersWithSameUsername = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(usersWithSameUsername).isZero();
    }
}
