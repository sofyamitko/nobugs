package ui.iteration2;

import api.asserts.ProfileSnapshot;
import api.models.admin.CreateUserRequestModel;
import api.models.authentication.LoginUserRequestModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateUserProfileTest {
    @BeforeAll
    public static void setupSelenoid() {
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
    public void userCanUpdateUserProfileTest() {
        // Шаги подготовки окружения
        // шаг 1 - создание юзера
        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // шаг 2 - изменение имени пользователя
        $(".user-name").click();

        $(Selectors.byAttribute("placeholder", "Enter new name")).doubleClick().setValue("Katya Karry");

        $$("button").findBy(Condition.exactText("💾 Save Changes")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Name updated successfully!");
        alert.accept();

        // шаг 3 - проверка, что имя изменено по UI
        Selenide.refresh();
        $(".user-name").shouldHave(Condition.text("Katya Karry"));

        //шаг 4 - проверка, что имя изменено на API
        //проверка изменения состояния через GET запрос профиля пользователя
        snapshot.assertThat().isChanged("Katya Karry");
    }

    @Test
    public void userCanNotUpdateUserProfileWithInvalidNameTest() {
        // Шаги подготовки окружения
        // шаг 1 - создание юзера
        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // шаг 2 - изменение имени пользователя
        $(".user-name").click();

        $(Selectors.byAttribute("placeholder", "Enter new name")).doubleClick().setValue("Katya    Karry");

        $$("button").findBy(Condition.exactText("💾 Save Changes")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("Name must contain two words with letters only");
        alert.accept();

        // шаг 3 - проверка, что имя не изменено по UI
        Selenide.refresh();
        $(".user-name").shouldHave(Condition.text("Noname"));

        //шаг 4 - проверка, что имя не изменено на API
        //проверка изменения состояния через GET запрос профиля пользователя
        snapshot.assertThat().isUnchanged();
    }

    @Test
    public void userCanNotUpdateUserProfileWithEmptyNameTest() {
        // Шаги подготовки окружения
        // шаг 1 - создание юзера
        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        // сохранение текущего состояния name до изменения
        ProfileSnapshot snapshot = ProfileSnapshot.of(user.getUsername(), user.getPassword());

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // шаг 2 - изменение имени пользователя
        $(".user-name").click();

        $$("button").findBy(Condition.exactText("💾 Save Changes")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please enter a valid name.");
        alert.accept();

        // шаг 3 - проверка, что имя не изменено по UI
        Selenide.refresh();
        $(".user-name").shouldHave(Condition.text("Noname"));

        //шаг 4 - проверка, что имя не изменено на API
        //проверка изменения состояния через GET запрос профиля пользователя
        snapshot.assertThat().isUnchanged();
    }
}
