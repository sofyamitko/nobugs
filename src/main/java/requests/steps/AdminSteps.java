package requests.steps;

import generators.RandomModelGenerator;
import models.admin.CreateUserRequestModel;
import models.admin.CreateUserResponseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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

}
