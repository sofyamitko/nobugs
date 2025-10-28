package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class NameChangeTest {
    private String tokenAuth;
    private String username;
    private final String baseUrl = "http://localhost:4111/api/v1";
    private static final String ADMIN_AUTH = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @BeforeEach
    public void createAccountsOfUser() {
        username = createUsername();
        tokenAuth = createUserAndReturnToken(username, "Password33!");
    }

    // служебный метод для создания неповторяющегося username для пользователя
    public String createUsername() {
        return "katya" + UUID.randomUUID().toString().substring(0, 4);
    }

    // служебный метод для создания нового пользователя и возврата его токена
    public String createUserAndReturnToken(String username, String password) {


        String requestBody = String.format(
                """ 
                               {
                                "username": "%s",
                                "password": "%s",
                                "role": "USER"
                               }
                        """, username, password);


        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", ADMIN_AUTH)
                .body(requestBody)
                .post(baseUrl + "/admin/users")
                .then()
                .statusCode(201);

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", ADMIN_AUTH)
                .body(requestBody)
                .post(baseUrl + "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .header("Authorization");
    }

    public String getNameUser(String token) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", token)
                .get(baseUrl + "/customer/profile")
                .then()
                .statusCode(200)
                .extract()
                .body().jsonPath().getString("name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Katya Katya", "KATYA KATYA", "Katya katya", "katya Katya", "d d"})
    public void userCanChangeValidName(String name) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, name))
                .put(baseUrl + "/customer/profile")
                .then()
                .statusCode(200)
                .assertThat()
                .body("message", Matchers.equalTo("Profile updated successfully"))
                .body("customer.username", Matchers.equalTo(this.username))
                .body("customer.name", Matchers.equalTo(name));

        String currentName = getNameUser(tokenAuth);
        Assertions.assertEquals(name, currentName);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Katya",
            "   ",
            "",
            "katya Katya2",
            "Katya! Katya",
            "Katya Katya)",
            "*Katya Katya",
            "Katya=| Katya",
            "katya , Katya",
            "katya  Katya",
            "katya Katya ",
            " katya Katya",
            "katya Katya Katya",
    })

    public void userCanNotChangeInvalidName(String name) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, name))
                .put(baseUrl + "/customer/profile")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo("Name must contain two words with letters only"));

        String currentName = getNameUser(tokenAuth);
        Assertions.assertNull( currentName);
    }
}
