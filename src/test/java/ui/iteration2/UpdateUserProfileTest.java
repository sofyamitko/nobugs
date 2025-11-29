package ui.iteration2;

import api.asserts.ProfileSnapshot;
import api.generators.RandomData;
import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.iteration1.BaseUITest;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;

public class UpdateUserProfileTest extends BaseUITest {

    @Test
    public void userCanUpdateUserProfileTest() {
        String name = RandomData.getName();

        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        new EditProfilePage().open()
                .enterName(name)
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .checkStateNameUser(name);

        //проверка изменения имени по API
        snapshot.assertThat().isChanged(name);
    }

    // Проверка валидации в связке с API - ошибка при изменении профиля с невалидным именем
    @ParameterizedTest
    @ValueSource(strings = {"Kata   Katya Kat"})
    public void userCanNotUpdateUserProfileWithInvalidNameTest(String name) {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        new EditProfilePage().open()
                .enterName(name)
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage())
                .checkStateNameUser("Noname");

        //проверка отсутствия изменения имени пользователя по API
        snapshot.assertThat().isUnchanged();
    }

    // Проверка валидации на UI - ошибка при изменении профиля с пустым именем
    @Test
    public void userCanNotUpdateUserProfileWithEmptyNameTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        new EditProfilePage().open()
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.ENTER_VALID_NAME.getMessage())
                .checkStateNameUser("Noname");

        //проверка отсутствия изменения имени пользователя по API
        snapshot.assertThat().isUnchanged();
    }
}