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

public class TransferMoneyTest {

    private int firstAccountId;
    private int secondAccountId;

    private String tokenAuth;
    private final double MAX_AMOUNT_FOR_DEPOSIT = 5000.00;

    private final String baseUrl = "http://localhost:4111/api/v1";
    private static final String ADMIN_AUTH = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    // перед каждым тестом создается новый пользователь, возвращается его токен
    // у пользователя создается 2 аккаунта, 1ый аккаунт пополнен на 10.000
    @BeforeEach
    public void createAccountsOfUser() {
        String firstUsername = createUsername();

        tokenAuth = createUserAndReturnToken(firstUsername, "Password33!");

        firstAccountId = createAccount(tokenAuth);
        secondAccountId = createAccount(tokenAuth);

        increaseBalanceOfAccount(tokenAuth, firstAccountId, MAX_AMOUNT_FOR_DEPOSIT);
        increaseBalanceOfAccount(tokenAuth, firstAccountId, MAX_AMOUNT_FOR_DEPOSIT);
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

    //служебный метод для создания аккаунта у пользователя и возврата id аккаунта
    public int createAccount(String token) {
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
    public double getAccountOfUser(String token, int id) {
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

    // служебный метод для пополнения баланса аккаунта
    public void increaseBalanceOfAccount(String token, int id, double amount) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", token)
                .body(String.format("""
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, id, amount))
                .post(baseUrl + "/accounts/deposit")
                .then()
                .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountBetweenOwnAccounts(double amount) {

        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, secondAccountId, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(200)
                .assertThat()
                .body("amount", Matchers.equalTo((float) amount))
                .body("receiverAccountId", Matchers.equalTo(secondAccountId))
                .body("senderAccountId", Matchers.equalTo(firstAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfSecondAccount = getAccountOfUser(tokenAuth, secondAccountId);
        Assertions.assertEquals(amount, actualBalanceOfSecondAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer - amount, actualBalanceOfFirstAccount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 5000})
    public void userCanTransferValidAmountBetweenOwnAccountsSeveralTimes(double amount) {

        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, secondAccountId, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(200)
                .assertThat()
                .body("amount", Matchers.equalTo((float) amount))
                .body("receiverAccountId", Matchers.equalTo(secondAccountId))
                .body("senderAccountId", Matchers.equalTo(firstAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, secondAccountId, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(200)
                .assertThat()
                .body("amount", Matchers.equalTo((float) amount))
                .body("receiverAccountId", Matchers.equalTo(secondAccountId))
                .body("senderAccountId", Matchers.equalTo(firstAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));


        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfSecondAccount = getAccountOfUser(tokenAuth, secondAccountId);
        Assertions.assertEquals(amount + amount, actualBalanceOfSecondAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer - amount - amount, actualBalanceOfFirstAccount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000.00})
    public void userCanTransferValidAmountToAnotherUsersAccount(double amount) {

        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);

        String anotherUsername = createUsername();
        String anotherToken = createUserAndReturnToken(anotherUsername, "Password33!");
        int anotherAccount = createAccount(anotherToken);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, anotherAccount, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(200)
                .assertThat()
                .body("amount", Matchers.equalTo((float) amount))
                .body("receiverAccountId", Matchers.equalTo(anotherAccount))
                .body("senderAccountId", Matchers.equalTo(firstAccountId))
                .body("message", Matchers.equalTo("Transfer successful"));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfAnotherAccount = getAccountOfUser(anotherToken, anotherAccount);
        Assertions.assertEquals(amount, actualBalanceOfAnotherAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer - amount, actualBalanceOfFirstAccount);
    }


    public static Stream<Arguments> invalidAmountForTransfer() {
        return Stream.of(
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(-1, "Transfer amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmountForTransfer")
    public void userCanNotTransferInvalidAmountBetweenOwnAccounts(double amount, String errorMessage) {

        increaseBalanceOfAccount(tokenAuth, firstAccountId, MAX_AMOUNT_FOR_DEPOSIT);

        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);
        double balanceOfSecondAccountBeforeTransfer = getAccountOfUser(tokenAuth, secondAccountId);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, secondAccountId, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo(errorMessage));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfSecondAccount = getAccountOfUser(tokenAuth, secondAccountId);
        Assertions.assertEquals(balanceOfSecondAccountBeforeTransfer, actualBalanceOfSecondAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer, actualBalanceOfFirstAccount);
    }

    public static Stream<Arguments> invalidAmountForTransferToAnotherAccount() {
        return Stream.of(
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(-1, "Transfer amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmountForTransferToAnotherAccount")
    public void userCanNotTransferInvalidAmountToAnotherUsersAccount(double amount, String errorMessage) {

        increaseBalanceOfAccount(tokenAuth, firstAccountId, MAX_AMOUNT_FOR_DEPOSIT);
        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);

        String anotherUsername = createUsername();
        String anotherToken = createUserAndReturnToken(anotherUsername, "Password33!");
        int anotherAccount = createAccount(anotherToken);
        double balanceOfAnotherAccountBeforeTransfer = getAccountOfUser(anotherToken, anotherAccount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": %s
                         }
                        """, firstAccountId, anotherAccount, amount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo(errorMessage));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfAnotherAccount = getAccountOfUser(anotherToken, anotherAccount);
        Assertions.assertEquals(balanceOfAnotherAccountBeforeTransfer, actualBalanceOfAnotherAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer, actualBalanceOfFirstAccount);
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceBetweenOwnAccounts() {

        double balanceOfFirstAccountBeforeTransfer = getAccountOfUser(tokenAuth, firstAccountId);
        double balanceOfSecondAccountBeforeTransfer = getAccountOfUser(tokenAuth, secondAccountId);


        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": 1
                         }
                        """, secondAccountId, firstAccountId))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfSecondAccount = getAccountOfUser(tokenAuth, secondAccountId);
        Assertions.assertEquals(balanceOfSecondAccountBeforeTransfer, actualBalanceOfSecondAccount);

        double actualBalanceOfFirstAccount = getAccountOfUser(tokenAuth, firstAccountId);
        Assertions.assertEquals(balanceOfFirstAccountBeforeTransfer, actualBalanceOfFirstAccount);
    }

    @Test
    public void userCanNotTransferAmountExceedingBalanceToAnotherUsersAccount() {

        double balanceOfSecondAccountBeforeTransfer = getAccountOfUser(tokenAuth, secondAccountId);

        String anotherUsername = createUsername();
        String anotherToken = createUserAndReturnToken(anotherUsername, "Password33!");
        int anotherAccount = createAccount(anotherToken);
        double balanceOfAnotherAccountBeforeTransfer = getAccountOfUser(anotherToken, anotherAccount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", tokenAuth)
                .body(String.format("""
                        {
                           "senderAccountId": %s,
                           "receiverAccountId": %s,
                           "amount": 1
                         }
                        """, secondAccountId, anotherAccount))
                .post(baseUrl + "/accounts/transfer")
                .then()
                .statusCode(400)
                .assertThat()
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        //проверка текущего баланса аккаунтов после перевода
        double actualBalanceOfAnotherAccount = getAccountOfUser(anotherToken, anotherAccount);
        Assertions.assertEquals(balanceOfAnotherAccountBeforeTransfer, actualBalanceOfAnotherAccount);

        double actualBalanceOfSecondAccount = getAccountOfUser(tokenAuth, secondAccountId);
        Assertions.assertEquals(balanceOfSecondAccountBeforeTransfer, actualBalanceOfSecondAccount);
    }
}
