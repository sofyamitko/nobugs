package requests;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class Request {
    //requests/ – отдельные классы-отправители запросов (Request).
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;

    public Request(RequestSpecification requestSpecification, ResponseSpecification responseSpecification){
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    /**
     * Убрала абстрактный метод post, создав отдельные интерфейсы с методами, тк
     * не во всех тестах требуется post запрос
     */
//    public abstract ValidatableResponse post(T model);
}
