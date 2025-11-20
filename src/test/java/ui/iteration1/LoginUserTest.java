package ui.iteration1;

import api.models.admin.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;


public class LoginUserTest {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.100:3000";
        Configuration.timeout = 60000;

        Configuration.browser = "chrome";
        Configuration.browserVersion = "91.0";
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));

    }

    @Test
    public void adminCanLoginWithCorrectDataTest(){
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest(){
        //create user
        CreateUserRequestModel user = AdminSteps.createUser();
        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
