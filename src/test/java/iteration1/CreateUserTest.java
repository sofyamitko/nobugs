package iteration1;

import generators.RandomData;
import models.admin.CreateUserRequestModel;
import models.admin.CreateUserResponseModel;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {


    @Test
    public void adminCanCreateUserWithCorrectDate() {

        CreateUserRequestModel createUserRequest = CreateUserRequestModel
                .builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponseModel createUserResponse = new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreatedSpec())
                .post(createUserRequest)
                .extract().as(CreateUserResponseModel.class);

        softly.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(createUserRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());
        softly.assertThat(createUserRequest.getRole()).isEqualTo(createUserResponse.getRole());


    }

    public static Stream<Arguments> invalidDate() {
        return Stream.of(
                Arguments.of(" ", "Password!3", "USER", "username",  new String[] {"Username must contain only letters, digits, dashes, underscores, and dots", "Username cannot be blank", "Username must be between 3 and 15 characters"}),
                Arguments.of("as", "Password!3", "USER", "username", new String[] {"Username must be between 3 and 15 characters"}),
                Arguments.of("as+!", "Password!3", "USER", "username", new String[] {"Username must contain only letters, digits, dashes, underscores, and dots"})

        );
    }

    @ParameterizedTest
    @MethodSource("invalidDate")
    public void adminCanNotCreateUserWithInvalidDate(String username, String password, String role, String errorkey, String[] errorValue) {
        CreateUserRequestModel createUserRequest = CreateUserRequestModel
                .builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsBadRequestSpec(errorkey, errorValue))
                .post(createUserRequest);

    }
}