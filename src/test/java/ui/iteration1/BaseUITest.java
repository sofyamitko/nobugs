package ui.iteration1;

import api.configs.Config;
import api.iteration1.BaseTest;
import api.models.admin.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith({BrowserMatchExtension.class})
public class BaseUITest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.timeout = 60000;
        Configuration.browser = Config.getProperty("uiBrowser");
        Configuration.browserVersion = Config.getProperty("browserVersion");
        Configuration.browserSize = Config.getProperty("browserSize");
//        Configuration.headless = true;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    public static void authAsUser(String username, String password){
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequestModel createUserRequestModel){
        authAsUser(createUserRequestModel.getUsername(), createUserRequestModel.getPassword());
    }

    public static void logout(){
        Selenide.open("/login");
        executeJavaScript("localStorage.removeItem('authToken');");
    }
}
