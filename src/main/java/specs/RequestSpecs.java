package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.authentication.LoginUserRequestModel;
import requests.LoginUserRequester;

import java.util.List;

public class RequestSpecs {
   // specs/ – отдельные классы для Request и Response спецификаций (стандартизация заголовков, кодов ответа).

    private RequestSpecs(){}

    private static RequestSpecBuilder defaultRequestBuilder(){
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri("http://localhost:4111");
    }

    public static RequestSpecification unauthSpec(){
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec(){
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    public static RequestSpecification authAsUserSpec(String username, String password){
        String userAuthHeader =  new LoginUserRequester(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(username).password(password).build())
                .extract()
                .header("Authorization");

        return defaultRequestBuilder()
                .addHeader("Authorization", userAuthHeader)
                .build();
    }
}
