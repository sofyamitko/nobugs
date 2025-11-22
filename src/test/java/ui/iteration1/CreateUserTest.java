package ui.iteration1;

import api.asserts.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateUserTest {
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
    public void adminCanCreateUserTest(){

        //шаг 1: админ залогинился
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        //шаг 2: админ создает юзера
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());
        $(Selectors.byText("Add User")).click();

        //шаг 3: проверка, что алерт "✅ User created successfully!"
        Alert alert = switchTo().alert();
        assertEquals(alert.getText(),"✅ User created successfully!");
        alert.accept();

        //шаг 4: проверка, что юзер отображается на ui

        ElementsCollection allUsersOfDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersOfDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        //шаг 5: проверка, что юзер создан на API
        CreateUserResponseModel[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(CreateUserResponseModel[].class);

        CreateUserResponseModel createdUser = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst()
                .get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }


    @Test
    public void adminCanNotCreateUserWithInvalidDataTest(){
        //шаг 1: админ залогинился
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        //шаг 2: админ создает юзера
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        newUser.setUsername("a");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());
        $(Selectors.byText("Add User")).click();


        //шаг 3: проверка, что алерт "Username must be between 3 and 15 characters"
        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("Username must be between 3 and 15 characters");
        alert.accept();

        //шаг 4: проверка, что юзер НЕ отображается на ui

        ElementsCollection allUsersOfDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersOfDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

        //шаг 5: проверка, что юзер НЕ создан на API
        CreateUserResponseModel[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(CreateUserResponseModel[].class);

        long usersWithSameUsername = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(usersWithSameUsername).isZero();
    }
}
