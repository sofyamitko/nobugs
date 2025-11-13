package requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {

    private final CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T post(BaseModel baseModel) {
        return (T) crudRequester.post(baseModel)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get(Integer id) {
        return (T) crudRequester.get(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester.get()
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel baseModel) {
        return (T) crudRequester.update(baseModel)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T delete(Integer id) {
        return null;
    }
}
