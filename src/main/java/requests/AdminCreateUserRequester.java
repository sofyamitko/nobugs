package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.admin.CreateUserRequestModel;
import requests.interfacesMethods.CreateRequesterUtil;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester extends Request implements CreateRequesterUtil<CreateUserRequestModel> {
    //requests/ – отдельные классы-отправители запросов (Request).
    public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateUserRequestModel model) {
        return  given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/admin/users")
                .then()
                .spec(responseSpecification);
    }
}
