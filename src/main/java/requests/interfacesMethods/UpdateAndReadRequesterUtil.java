package requests.interfacesMethods;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface UpdateAndReadRequesterUtil<T extends BaseModel> extends ReadRequesterUtil {
    ValidatableResponse put(T model);
}
