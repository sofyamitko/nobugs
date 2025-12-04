package common.extensions;

import api.models.admin.CreateUserRequestModel;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static ui.pages.BasePage.authAsUser;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        //шаг 1 = проверка есть ли у теста аннотация AdminSession
        AdminSession annotation = context.getRequiredTestMethod().getAnnotation(AdminSession.class);

        if(annotation != null){ // шаг 2 = если есть, добавляем токен в локал сторадж
            authAsUser(CreateUserRequestModel.getAdmin());
        }
    }
}
