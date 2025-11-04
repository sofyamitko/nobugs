package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.authentication.LoginUserRequestModel;
import requests.interfacesMethods.CreateRequesterUtil;

import static io.restassured.RestAssured.given;

public class LoginUserRequester extends Request implements CreateRequesterUtil<LoginUserRequestModel> {
    //requests/ – отдельные классы-отправители запросов (Request).
    public LoginUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(LoginUserRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/auth/login")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
