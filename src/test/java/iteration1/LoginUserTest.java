package iteration1;

import models.admin.CreateUserRequestModel;
import models.admin.CreateUserResponseModel;
import models.authentication.LoginUserRequestModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


public class LoginUserTest extends BaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {

        LoginUserRequestModel userRequest = LoginUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {

        CreateUserRequestModel createUserRequest = AdminSteps.createUser();

        new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.LOGIN, ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel
                        .builder()
                        .username(createUserRequest.getUsername())
                        .password(createUserRequest.getPassword())
                        .build())
                .header("Authorization", Matchers.notNullValue());
    }
}