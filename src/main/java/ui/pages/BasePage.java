package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;

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
}
