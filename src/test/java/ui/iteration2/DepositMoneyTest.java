package ui.iteration2;

import api.models.accounts.AccountResponseModel;
import api.models.admin.CreateUserRequestModel;
import api.models.authentication.LoginUserRequestModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import com.codeborne.selenide.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DepositMoneyTest {
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
    public void userCanIncreaseDepositTest() {

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

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // шаг 2 - создание аккаунта
        AccountResponseModel account = UserSteps.createAccount(user);

        // Шаги теста
        // шаг 3 - пополнение депозита
        Selenide.open("/dashboard");
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        //проверка, что в списке есть только 1 аккаунт
        ElementsCollection elementsOptions = $$(".account-selector option");
        ElementsCollection filtered = elementsOptions.filter(Condition.text("ACC"));
        int count = filtered.size();
        assertThat(count).isEqualTo(1);

        SelenideElement select = $x("//label[text()='Select Account:']/following-sibling::select");
        select.selectOptionContainingText(account.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys("100");
        $$("button").findBy(Condition.exactText("\uD83D\uDCB5 Deposit")).click();

        // шаг 4 - проверка, что аккаунт пополнен по UI
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Successfully deposited $100 to account " + account.getAccountNumber() + "!");
        alert.accept();

        // шаг 5 - проверка, что аккаунт пополнен по API
        AccountResponseModel[] accounts = given()
                .spec(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .extract().as(AccountResponseModel[].class);
        assertThat(accounts).hasSize(1);

        boolean checkingBalanceResult = Arrays.stream(accounts)
                .filter(acc -> acc.getAccountNumber().equals(account.getAccountNumber()))
                .anyMatch(acc -> acc.getBalance() == 100);
        assertTrue(checkingBalanceResult);
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненным аккаунтом
    @Test
    public void userCanNotIncreaseDepositWithEmptyAccountTest() {

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

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // шаг 2 - создание аккаунта
        AccountResponseModel account = UserSteps.createAccount(user);

        // Шаги теста
        // шаг 3 - пополнение депозита
        Selenide.open("/dashboard");
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        //проверка, что в списке есть только 1 аккаунт
        ElementsCollection elementsOptions = $$(".account-selector option");
        ElementsCollection filtered = elementsOptions.filter(Condition.text("ACC"));
        int count = filtered.size();
        assertThat(count).isEqualTo(1);

        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys("100");
        $$("button").findBy(Condition.exactText("\uD83D\uDCB5 Deposit")).click();

        // шаг 4 - проверка появления ошибки по UI
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please select an account.");
        alert.accept();

        // шаг 5 - проверка, что аккаунт НЕ пополнен по API
        AccountResponseModel[] accounts = given()
                .spec(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .extract().as(AccountResponseModel[].class);
        assertThat(accounts).hasSize(1);

        Assertions.assertThat(accounts[0].getBalance()).isZero();
    }

    // Проверка валидации на UI - ошибка при пополнении депозита с незаполненной суммой
    @Test
    public void userCanNotIncreaseDepositWithEmptyAmountTest() {

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

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // шаг 2 - создание аккаунта
        AccountResponseModel account = UserSteps.createAccount(user);

        // Шаги теста
        // шаг 3 - пополнение депозита
        Selenide.open("/dashboard");
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        //проверка, что в списке есть только 1 аккаунт
        ElementsCollection elementsOptions = $$(".account-selector option");
        ElementsCollection filtered = elementsOptions.filter(Condition.text("ACC"));
        int count = filtered.size();
        assertThat(count).isEqualTo(1);

        SelenideElement select = $x("//label[text()='Select Account:']/following-sibling::select");
        select.selectOptionContainingText(account.getAccountNumber());
        $$("button").findBy(Condition.exactText("\uD83D\uDCB5 Deposit")).click();

        // шаг 4 - проверка появления ошибки по UI
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please enter a valid amount.");
        alert.accept();

        // шаг 5 - проверка, что аккаунт НЕ пополнен по API
        AccountResponseModel[] accounts = given()
                .spec(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .extract().as(AccountResponseModel[].class);
        assertThat(accounts).hasSize(1);

        Assertions.assertThat(accounts[0].getBalance()).isZero();
    }

    // Проверка валидации на API (интеграция) - ошибка при пополнении депозита с невалидной суммой
    @Test
    public void userCanNotIncreaseDepositWithInvalidAmountTest() {

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

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // шаг 2 - создание аккаунта
        AccountResponseModel account = UserSteps.createAccount(user);

        // Шаги теста
        // шаг 3 - пополнение депозита
        Selenide.open("/dashboard");
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        //проверка, что в списке есть только 1 аккаунт
        ElementsCollection elementsOptions = $$(".account-selector option");
        ElementsCollection filtered = elementsOptions.filter(Condition.text("ACC"));
        int count = filtered.size();
        assertThat(count).isEqualTo(1);

        SelenideElement select = $x("//label[text()='Select Account:']/following-sibling::select");
        select.selectOptionContainingText(account.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys("5001");
        $$("button").findBy(Condition.exactText("\uD83D\uDCB5 Deposit")).click();

        // шаг 4 - проверка появления ошибки по UI
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please deposit less or equal to 5000$.");
        alert.accept();

        // шаг 5 - проверка, что аккаунт НЕ пополнен по API
        AccountResponseModel[] accounts = given()
                .spec(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .extract().as(AccountResponseModel[].class);
        assertThat(accounts).hasSize(1);

        Assertions.assertThat(accounts[0].getBalance()).isZero();
    }
}
