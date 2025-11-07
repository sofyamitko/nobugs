package iteration2;

import iteration1.BaseTest;
import models.customer.GetUserProfileResponseModel;
import models.customer.UpdateUserProfileRequestModel;
import models.customer.UpdateUserProfileResponseModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.GetAndUpdateCustomerProfileRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.factory.TestUserContext;

public class UpdateUserProfileTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {"Katya Katya", "KATYA KATYA", "Katya katya", "katya Katya", "d d"})
    public void userCanUpdateUserProfile(String name) {

        TestUserContext testUser = factory.createUser();

        // создание тела запроса на изменение имени пользователя
        UpdateUserProfileRequestModel userProfileRequest =  UpdateUserProfileRequestModel
                .builder()
                .name(name)
                .build();

        UpdateUserProfileResponseModel updateUserProfileResponse = new GetAndUpdateCustomerProfileRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsOkSpec())
                .put(userProfileRequest)
                .extract()
                .as(UpdateUserProfileResponseModel.class);

        //проверка тела ответа
        softly.assertThat(updateUserProfileResponse.getMessage()).isEqualTo("Profile updated successfully");
        softly.assertThat(updateUserProfileResponse.getCustomer().getUsername()).isEqualTo(testUser.getUsername());
        softly.assertThat(updateUserProfileResponse.getCustomer().getName()).isEqualTo(name);

        //проверка изменения состояния через GET запрос профиля пользователя
        GetUserProfileResponseModel getUserProfileResponse = userUtils.getUserProfile(testUser.getUsername(), testUser.getPassword());
        softly.assertThat(getUserProfileResponse.getName()).isEqualTo(name);
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

        TestUserContext testUser = factory.createUser();

        // создание тела запроса на изменение имени пользователя
        UpdateUserProfileRequestModel userProfileRequest =  UpdateUserProfileRequestModel
                .builder()
                .name(name)
                .build();

        new GetAndUpdateCustomerProfileRequester(
                RequestSpecs.authAsUserSpec(testUser.getUsername(), testUser.getPassword()),
                ResponseSpecs.requestReturnsBadRequestSpec("Name must contain two words with letters only"))
                .put(userProfileRequest);

        //проверка изменения состояния через GET запрос профиля пользователя
        GetUserProfileResponseModel getUserProfileResponse = userUtils.getUserProfile(testUser.getUsername(), testUser.getPassword());
        softly.assertThat(getUserProfileResponse.getName()).isNull();
    }
}