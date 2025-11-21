package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class AdminSteps {

    public static CreateUserRequestModel createUser() {

        CreateUserRequestModel createUserRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        new ValidatedCrudRequester<CreateUserResponseModel>
                (RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreatedSpec())
                .post(createUserRequest);
        return createUserRequest;
    }

    public static List<CreateUserResponseModel> getAllUsers(){
        return new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOkSpec())
                .getAll(CreateUserResponseModel[].class);
    }
}
