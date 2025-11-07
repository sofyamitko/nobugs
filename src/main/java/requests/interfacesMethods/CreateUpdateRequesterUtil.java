package requests.interfacesMethods;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

//для Requester, поддерживающих get() и post()
public interface CreateUpdateRequesterUtil<T extends BaseModel> extends CreateRequesterUtil<T> {
    ValidatableResponse get();
}
