package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

public class ResponseSpecs {
    // specs/ – отдельные классы для Request и Response спецификаций (стандартизация заголовков, кодов ответа).

    private ResponseSpecs(){}

    private static ResponseSpecBuilder defaultResponseBuilder(){
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreatedSpec(){
        return defaultResponseBuilder()
                .expectStatusCode(201)
                .build();
    }

    public static ResponseSpecification requestReturnsOkSpec(){
        return defaultResponseBuilder()
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequestSpec(String errorkey, String[] errorValues){
        return defaultResponseBuilder()
                .expectStatusCode(400)
                .expectBody(errorkey, Matchers.containsInAnyOrder(errorValues))
                .build();
    }
    public static ResponseSpecification requestReturnsBadRequestSpec(String errorValue){
        return defaultResponseBuilder()
                .expectStatusCode(400)
                .expectBody( Matchers.equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestForbiddenSpec(String errorValue){
        return defaultResponseBuilder()
                .expectStatusCode(403)
                .expectBody(Matchers.equalTo(errorValue))
                .build();
    }
}