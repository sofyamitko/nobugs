package ui.pages;

import api.models.admin.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.*;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public abstract class BasePage<T extends BasePage> {

    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput =  $(Selectors.byAttribute("placeholder", "Password"));
    protected SelenideElement nameUserSpan = $x("//div[@class='user-info']//span[@class='user-name']");

    public abstract String url();

    public T open(){
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass){
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String bankAlert){
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();
        return (T) this;
    }

    public T checkStateNameUser(String name){
        Selenide.refresh();
        nameUserSpan.shouldHave(Condition.exactText(name));
        return (T) this;
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

    //ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor){
        return elementsCollection.stream()
                .map(constructor)
                .toList();
    }
}
