package api.iteration1;

import api.asserts.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectDate() {

        CreateUserRequestModel createUserRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel createUserResponse = new ValidatedCrudRequester<CreateUserResponseModel>
                (RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreatedSpec())
                .post(createUserRequest);

        ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
    }

    public static Stream<Arguments> invalidDate() {
        return Stream.of(
                Arguments.of(" ", "Password!3", "USER", "username", new String[]{"Username must contain only letters, digits, dashes, underscores, and dots", "Username cannot be blank", "Username must be between 3 and 15 characters"}),
                Arguments.of("as", "Password!3", "USER", "username", new String[]{"Username must be between 3 and 15 characters"}),
                Arguments.of("as+!", "Password!3", "USER", "username", new String[]{"Username must contain only letters, digits, dashes, underscores, and dots"})

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

        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequestSpec(errorkey, errorValue))
                .post(createUserRequest);
    }
}