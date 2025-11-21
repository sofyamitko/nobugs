package api.iteration1;

import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.models.authentication.LoginUserRequestModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


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