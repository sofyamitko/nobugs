package api.iteration2;

import api.asserts.ProfileSnapshot;
import api.asserts.comparison.ModelAssertions;
import api.iteration1.BaseTest;
import api.models.admin.CreateUserRequestModel;
import api.models.customer.UpdateUserProfileRequestModel;
import api.models.customer.UpdateUserProfileResponseModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class UpdateUserProfileTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {"Katya Katya", "KATYA KATYA", "Katya katya", "katya Katya", "d d"})
    public void userCanUpdateUserProfile(String name) {

        CreateUserRequestModel user = AdminSteps.createUser();

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        // создание тела запроса на изменение имени пользователя
        UpdateUserProfileRequestModel userProfileRequest = UpdateUserProfileRequestModel
                .builder()
                .name(name)
                .build();

        UpdateUserProfileResponseModel userProfileResponse = new ValidatedCrudRequester<UpdateUserProfileResponseModel>(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.UPDATE_PROFILE,
                ResponseSpecs.requestReturnsOkSpec())
                .update(userProfileRequest);

        //проверка тела ответа
        ModelAssertions.assertThatModels(userProfileRequest, userProfileResponse).match();

        //проверка изменения состояния через GET запрос профиля пользователя
        snapshot.assertThat().isChanged(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Katya",
            "   ",
            "",
            "katya Katya2",
            "Katya! Katya",
            "Katya Katya)",
            "*Katya Katya",
            "Katya=| Katya",
            "katya , Katya",
            "katya  Katya",
            "katya Katya ",
            " katya Katya",
            "katya Katya Katya",
    })

    public void userCanNotUpdateUserProfile(String name) {

        CreateUserRequestModel user = AdminSteps.createUser();

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        // создание тела запроса на изменение имени пользователя
        UpdateUserProfileRequestModel userProfileRequest = UpdateUserProfileRequestModel
                .builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.UPDATE_PROFILE,
                ResponseSpecs.requestReturnsBadRequestSpec("Name must contain two words with letters only"))
                .update(userProfileRequest);

        //проверка отсутствия изменения состояния через GET запрос профиля пользователя
        snapshot.assertThat().isUnchanged();
    }
}