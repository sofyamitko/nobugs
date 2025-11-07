package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.accounts.AccountRequestModel;
import requests.interfacesMethods.CreateRequesterUtil;

import static io.restassured.RestAssured.given;

public class DepositAccountRequester extends Request implements CreateRequesterUtil<AccountRequestModel> {

    public DepositAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(AccountRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/accounts/deposit")
                .then()
                .spec(responseSpecification);
    }

}
