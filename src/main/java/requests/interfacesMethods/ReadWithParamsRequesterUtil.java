package requests.interfacesMethods;

import io.restassured.response.ValidatableResponse;

public interface ReadWithParamsRequesterUtil {
    ValidatableResponse get(int param);
}
