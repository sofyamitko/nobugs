package iteration1;

import models.admin.CreateUserRequestModel;
import org.junit.jupiter.api.Test;

import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


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