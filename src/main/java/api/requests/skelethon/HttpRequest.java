package api.requests.skelethon;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class HttpRequest {
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;
    protected Endpoint endpoint;

    public HttpRequest( RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification ){
        this.requestSpecification = requestSpecification;
        this.endpoint = endpoint;
        this.responseSpecification = responseSpecification;
    }
}
