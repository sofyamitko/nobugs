package common.extensions;

import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.UserSessionWithAccounts;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

public class UserSessionWithAccountsExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserSessionWithAccounts annotation =
                context.getRequiredTestMethod().getAnnotation(UserSessionWithAccounts.class);

        if (annotation != null) {

            int userCount = annotation.value();     // сколько пользователей создать
            int authAsUser = annotation.auth();     // под каким авторизоваться
            int accountsOfUser = annotation.accounts(); // сколько аккаунтов на пользователя

            SessionStorage.clear();

            // 1) создаём пользователей
            List<CreateUserRequestModel> users = new LinkedList<>();
            for (int i = 0; i < userCount; i++) {
                users.add(AdminSteps.createUser());
            }

            // 2) сохраняем в storage
            SessionStorage.addUsers(users);

            // 3) создаём аккаунты для каждого пользователя
            for (int i = 1; i <= userCount; i++) {

                CreateUserRequestModel userModel = SessionStorage.getUser(i);

                for (int j = 0; j < accountsOfUser; j++) {
                    UserSteps.createAccount(userModel);
                }
            }

            // 4) авторизация в UI
            BasePage.authAsUser(SessionStorage.getUser(authAsUser));
        }
    }
}
