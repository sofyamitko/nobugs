package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class DepositMoneyTest {

    private int accountId;
    private String tokenAuth;

    private final String baseUrl = "http://localhost:4111/api/v1";
    private static final String ADMIN_AUTH = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setup(){
         RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    // перед каждым тестом создается новый пользователь, возвращается его токен и создается по нему аккаунт
    @BeforeEach
    public void createAccountOfUser(){
        String username = createUsername();
        tokenAuth = createUserAndReturnToken(username, "Password33!");
        accountId = createAccount(tokenAuth);
    }

    // служебный метод для создания неповторяющегося username для пользователя
    public String createUsername(){
        return "katya" + UUID.randomUUID().toString().substring(0, 4);
    }

    // служебный метод для создания нового пользователя и возврата его токена
    public String createUserAndReturnToken(String username, String password){


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

    //служебный метод для создания аккаунта у пользователя
    public int createAccount(String token){
       return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", token)
                .post(baseUrl + "/accounts")
                .then()
                .statusCode(201)
                .extract()
                .body().jsonPath().getInt("id");
    }

    // служебный метод для получения баланса у конкретного аккаунта пользователя
    public double getAccountOfUser(String token, int id){
      return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", token)
                .get(baseUrl + "/customer/accounts")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getDouble("find {it.id == " + id + "}.balance");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanIncreaseDepositOfExistingAccount(double amount){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, accountId, amount))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(200)
                .assertThat()
                .body("balance", Matchers.equalTo((float) amount));

        // проверка через GET запрос, что баланс пользователя увеличился на сумму депозита
        double actualBalance = getAccountOfUser(tokenAuth, accountId);
        Assertions.assertEquals(amount, actualBalance);
    }


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanIncreaseDepositOfExistingAccountSeveralTimes(double amount){

        // 1ое увеличения баланса
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, accountId, amount))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(200);

        // 2ое увеличение баланса
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, accountId, amount))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(200);

        // проверка через GET запрос, что баланс пользователя увеличился на сумму депозита дважды
        double actualBalance = getAccountOfUser(tokenAuth, accountId);
        Assertions.assertEquals(amount+amount, actualBalance);
    }

    public static Stream<Arguments> invalidAmount(){

        return Stream.of(
                Arguments.of(0.0, "Deposit amount must be at least 0.01"),
                Arguments.of(-2, "Deposit amount must be at least 0.01"),
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCanNotIncreaseDepositOfExistingAccountByInvalidAmount(double amount, String errorMessage){

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, accountId, amount))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo(errorMessage));

        // проверка через GET запрос, что баланс пользователя НЕ изменился на сумму депозита из-за некорректной суммы
        double actualBalance = getAccountOfUser(tokenAuth, accountId);
        Assertions.assertEquals(0.0, actualBalance);
    }


    @Test
    public void userCanNotIncreaseDepositOfAnotherAccountByValidAmount(){

        //создание дополнительного пользователя с аккаунтом
        String username = createUsername();
        String anotherTokenAuth = createUserAndReturnToken(username, "Password33!");
        int anotherAccountId = createAccount(anotherTokenAuth);

        //попытка пополнить первым пользователем баланс счета второго пользователя
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": 50
                        }
                        """, anotherAccountId))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(403)
                .assertThat()
                .body(Matchers.equalTo("Unauthorized access to account"));

        // проверка через GET запрос, что чужой баланс счета НЕ изменен при пополнении его другим пользователем
        double actualBalanceOfAnotherAccount = getAccountOfUser(anotherTokenAuth, anotherAccountId);
        Assertions.assertEquals(0.0, actualBalanceOfAnotherAccount);

        // проверка через GET запрос, что баланс счета пользователя, осуществляющего депозит,
        // НЕ изменен, тк указан чужой id счета
        double actualBalanceOfAccount = getAccountOfUser(tokenAuth, accountId);
        Assertions.assertEquals(0.0, actualBalanceOfAccount);
    }

    @Test
    public void userCanNotIncreaseDepositOfNotExistingAccountByValidAmount(){

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body("""
                        {
                          "id": 0,
                          "balance": 50
                        }
                        """)
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(403)
                .assertThat()
                .body(Matchers.equalTo("Unauthorized access to account"));

        // проверка через GET запрос, что баланс счета пользователя, осуществляющего депозит,
        // НЕ изменен, тк указан несуществующий id счета
        double actualBalance = getAccountOfUser(tokenAuth, accountId);
        Assertions.assertEquals(0.0, actualBalance);
    }
}