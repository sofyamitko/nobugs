package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement accountSelect = $x("//label[text()='Select Account:']/following-sibling::select");
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage selectAccount(String idAccount) {
        accountSelect.selectOptionContainingText(idAccount);
        return this;
    }

    public DepositPage enterMoney(double amount) {
        amountInput.setValue(amount+"");
        return this;
    }

    public DepositPage submitDeposit() {
        depositButton.click();
        return this;
    }
}
