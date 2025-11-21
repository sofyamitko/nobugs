package ui.iteration2;

import api.asserts.ProfileSnapshot;
import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;
import ui.iteration1.BaseUITest;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;

public class UpdateUserProfileTest extends BaseUITest {

    @Test
    public void userCanUpdateUserProfileTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        new EditProfilePage().open()
                .enterName("Kata Katya")
                .saveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .checkStateNameUser("Kata Katya");

        //проверка изменения имени по API
        snapshot.assertThat().isChanged("Kata Katya");
    }

    // Проверка валидации в связке с API - ошибка при изменении профиля с невалидным именем
    @Test
    public void userCanNotUpdateUserProfileWithInvalidNameTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        authAsUser(user);

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        new EditProfilePage().open()
                .enterName("Kata   Katya Kat")
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