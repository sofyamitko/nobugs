package api.requests.skelethon.requesters;

import api.requests.skelethon.interfaces.GetAllEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

/**
 Сериализация — это преобразование объекта в JSON.
 Десериализация — преобразование JSON назад в объект.
 В RestAssured метод .as(Class) выполняет десериализацию: превращает тело ответа в Java-объект указанного класса.
 */

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {

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

    @Override
    public List<T> getAll(Class<?> clazz) {
        T[] array = (T[]) crudRequester.getAll(clazz).extract().as(clazz);
        return Arrays.asList(array);
    }
}
