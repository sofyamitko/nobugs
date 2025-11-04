package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.interfacesMethods.UpdateAndReadRequesterUtil;

import static io.restassured.RestAssured.given;

public class GetAndUpdateCustomerProfileRequester extends Request implements UpdateAndReadRequesterUtil {

    public GetAndUpdateCustomerProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse put(BaseModel model) {
        return  given()
                .spec(requestSpecification)
                .body(model)
                .put("/api/v1/customer/profile")
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return  given()
                .spec(requestSpecification)
                .get("/api/v1/customer/profile")
                .then()
                .spec(responseSpecification);
    }
}