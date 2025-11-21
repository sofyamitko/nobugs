package ui.iteration1;

import api.configs.Config;
import api.iteration1.BaseTest;
import api.models.admin.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUITest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.timeout = 6000;
        Configuration.browser = Config.getProperty("uiBrowser");
        Configuration.browserVersion = Config.getProperty("browserVersion");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    public void authAsUser(String username, String password){
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public void authAsUser(CreateUserRequestModel createUserRequestModel){
        authAsUser(createUserRequestModel.getUsername(), createUserRequestModel.getPassword());
    }

    public void logout(){
        Selenide.open("/login");
        executeJavaScript("localStorage.removeItem('authToken');");
    }
}
