package requests.interfacesMethods;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface CreateRequesterUtil<T extends BaseModel> {
     ValidatableResponse post(T model);
}
