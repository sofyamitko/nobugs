package iteration2;

import asserts.ProfileSnapshot;
import asserts.comparison.ModelAssertions;
import iteration1.BaseTest;
import models.admin.CreateUserRequestModel;
import models.customer.UpdateUserProfileRequestModel;
import models.customer.UpdateUserProfileResponseModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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