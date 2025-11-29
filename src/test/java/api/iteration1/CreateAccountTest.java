package api.iteration1;

import api.models.admin.CreateUserRequestModel;
import org.junit.jupiter.api.Test;

import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccount() {

        CreateUserRequestModel createUserRequest = AdminSteps.createUser();
        new CrudRequester(
                RequestSpecs.authAsUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);
    }
}