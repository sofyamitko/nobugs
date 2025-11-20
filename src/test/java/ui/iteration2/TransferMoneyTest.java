package ui.iteration2;

import api.asserts.AccountBalanceSnapshot;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferMoneyTest {
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
    public void userCanTransferAmountBetweenOwnAccountsTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Successfully transferred $50 to account " + account2.getAccountNumber() + "!");

        // шаг 4 - проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $14950.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $50.00")).shouldBe(visible);

        // шаг 5 - проверка по API
        balanceSenderAccount.assertThat().isDecreasedBy(50);
        balanceReceiverAccount.assertThat().isIncreasedBy(50);
    }

    @Test
    public void userCanTransferAmountWithEmptyRecipientNameTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Successfully transferred $50 to account " + account2.getAccountNumber() + "!");

        // шаг 4 - проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $14950.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $50.00")).shouldBe(visible);

        // шаг 5 - проверка по API
        balanceSenderAccount.assertThat().isDecreasedBy(50);
        balanceReceiverAccount.assertThat().isIncreasedBy(50);
    }

    //Негативная проверка по переводу невалидной суммы на другой аккаунт
    @Test
    public void userCanNotTransferInvalidAmountBetweenOwnAccountsTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 4 - выполнение перевода
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("0");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Error: Transfer amount must be at least 0.01");

        // шаг 5 - проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $0.00")).shouldBe(visible);

        // шаг 6 - проверка по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу суммы c пустым отправителем
    @Test
    public void userCanNotTransferWithEmptySenderAccountTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3 - - выполнение перевода
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("10");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please fill all fields and confirm.");

        // шаг 4 - проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $0.00")).shouldBe(visible);

        // шаг 5 - проверка по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }


    //Негативная проверка по переводу суммы c пустым получателем
    @Test
    public void userCanNotTransferWithEmptyRecipientAccountTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();


        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please fill all fields and confirm.");

        // проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $0.00")).shouldBe(visible);

        //проверка по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу с пустой суммой
    @Test
    public void userCanNotTransferWithEmptyAmountTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();


        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please fill all fields and confirm.");

        // проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $0.00")).shouldBe(visible);

        //проверка по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    //Негативная проверка по переводу с неотмеченным чекбоксом
    @Test
    public void userCanNotTransferWithEmptyConfirmationCheckboxTest() {
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

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user);
        AccountResponseModel account2 = UserSteps.createAccount(user);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user.getUsername(), user.getPassword(), account1.getId(), 15000);
        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user.getUsername(), user.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("❌ Please fill all fields and confirm.");

        // проверка по UI
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).shouldBe(visible);
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $0.00")).shouldBe(visible);

        //проверка по API
        balanceSenderAccount.assertThat().isUnchanged();
        balanceReceiverAccount.assertThat().isUnchanged();
    }

    @Test
    public void userCanTransferValidAmountToAnotherUsersAccountTest() {
        // Шаги подготовки окружения
        // шаг 1 - создание 2ух юзера
        CreateUserRequestModel user1 = AdminSteps.createUser();
        CreateUserRequestModel user2 = AdminSteps.createUser();

        String userAuthHeader1 = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user1.getUsername()).password(user1.getPassword()).build())
                .extract()
                .header("Authorization");

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user1);
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user1.getUsername(), user1.getPassword(), account1.getId(), 15000);

        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader1);

        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue("Noname");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Successfully transferred $50 to account " + account2.getAccountNumber() + "!");

        // шаг 4 - проверка по UI
        // проверка изменения баланса первого юзера
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $14950.00)")).shouldBe(visible);

        // проверка изменения баланса первого юзера c удалением токена первого юзера и логином второго юзера
        $(Selectors.byText("\uD83D\uDEAA Logout")).click();
        executeJavaScript("localStorage.removeItem('authToken');");

        String userAuthHeader2 = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user2.getUsername()).password(user2.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader2);

        Selenide.open("/deposit");
        SelenideElement parentAfterTransfer2 = $(".account-selector");

        parentAfterTransfer2.click();
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $50.00)")).shouldBe(visible);

        // шаг 5 - проверка по API
        balanceSenderAccount.assertThat().isDecreasedBy(50);
        balanceReceiverAccount.assertThat().isIncreasedBy(50);
    }

    @Test
    public void userCanNotTransferToAnotherUsersAccountWithInvalidRecipientNameTest() {
        // Шаги подготовки окружения
        // шаг 1 - создание 2ух юзера
        CreateUserRequestModel user1 = AdminSteps.createUser();
        CreateUserRequestModel user2 = AdminSteps.createUser();

        String userAuthHeader1 = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user1.getUsername()).password(user1.getPassword()).build())
                .extract()
                .header("Authorization");

        // шаг 2 - создание 2ух аккаунтов
        AccountResponseModel account1 = UserSteps.createAccount(user1);
        AccountResponseModel account2 = UserSteps.createAccount(user2);

        // шаг 3 - пополнение первого аккаунта
        UserSteps.depositAccount(user1.getUsername(), user1.getPassword(), account1.getId(), 15000);

        // создание снэпшота текущего состояния баланса (до выполнения перевода)
        AccountBalanceSnapshot balanceSenderAccount = AccountBalanceSnapshot.of(user1.getUsername(), user1.getPassword(), account1.getId());
        AccountBalanceSnapshot balanceReceiverAccount = AccountBalanceSnapshot.of(user2.getUsername(), user2.getPassword(), account2.getId());

        //Шаги теста
        //шаг 3
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader1);

        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();

        SelenideElement parentBeforeTransfer = $(".account-selector");
        parentBeforeTransfer.$$("option").findBy(text(account1.getAccountNumber() + " (Balance: $15000.00)")).click();

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(user1.getUsername());
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue("50");

        $("#confirmCheck").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ Successfully transferred $50 to account " + account2.getAccountNumber() + "!");

        // шаг 4 - проверка по UI
        // проверка изменения баланса первого юзера
        Selenide.refresh();
        SelenideElement parentAfterTransfer = $(".account-selector");

        parentAfterTransfer.click();
        $$("option").findBy(text(account1.getAccountNumber() + " (Balance: $14950.00)")).shouldBe(visible);

        // проверка изменения баланса первого юзера c удалением токена первого юзера и логином второго юзера
        $(Selectors.byText("\uD83D\uDEAA Logout")).click();
        executeJavaScript("localStorage.removeItem('authToken');");

        String userAuthHeader2 = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOkSpec())
                .post(LoginUserRequestModel.builder().username(user2.getUsername()).password(user2.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader2);

        Selenide.open("/deposit");
        SelenideElement parentAfterTransfer2 = $(".account-selector");

        parentAfterTransfer2.click();
        $$("option").findBy(text(account2.getAccountNumber() + " (Balance: $50.00)")).shouldBe(visible);

        // шаг 5 - проверка по API
        balanceSenderAccount.assertThat().isDecreasedBy(50);
        balanceReceiverAccount.assertThat().isIncreasedBy(50);
    }
}
