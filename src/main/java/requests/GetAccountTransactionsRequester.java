package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import requests.interfacesMethods.ReadRequesterUtil;
import requests.interfacesMethods.ReadWithParamsRequesterUtil;

import static io.restassured.RestAssured.given;

public class GetAccountTransactionsRequester extends Request implements ReadWithParamsRequesterUtil {

    public GetAccountTransactionsRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse get(int id) {
        return  given()
                .spec(requestSpecification)
                .pathParam("accountId", id)
                .get("/api/v1/accounts/{accountId}/transactions" )
                .then()
                .spec(responseSpecification);
    }
}
