package iteration1;

import generators.RandomData;
import models.admin.CreateUserRequestModel;
import models.authentication.LoginUserRequestModel;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static io.restassured.RestAssured.given;

public class LoginUserTest extends BaseTest{

    @Test
    public void adminCanGenerateAuthTokenTest() {

        LoginUserRequestModel userRequest = LoginUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        new LoginUserRequester(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOkSpec())
                .post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {

        CreateUserRequestModel userRequest = CreateUserRequestModel.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreatedSpec())
                .post(userRequest);


        new LoginUserRequester(RequestSpecs.unauthSpec(), ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel
                        .builder()
                        .username(userRequest.getUsername())
                        .password(userRequest.getPassword())
                        .build())
                .header("Authorization", Matchers.notNullValue());
    }
}