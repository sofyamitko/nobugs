package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TransferPage extends BasePage<TransferPage> {

    private SelenideElement senderAccountSelector =
            $x("//label[text()='Select Your Account:']/following-sibling::select");

    private SelenideElement recipientNameInput = $(Selectors.byAttribute("placeholder", "Enter recipient name"));

    private SelenideElement recipientAccountNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));

    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));

    private SelenideElement confirmCheckbox = $("#confirmCheck");

    private SelenideElement transferButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage selectSenderAccount(String senderAccountId) {
        senderAccountSelector.selectOptionContainingText(senderAccountId);
        return this;
    }

    public TransferPage enterRecipientName(String name) {
        recipientNameInput.shouldBe(Condition.interactable).setValue(name);
        return this;
    }

    public TransferPage enterRecipientAccountNumber(String recipientAccountId) {
        recipientAccountNumberInput.shouldBe(Condition.interactable).setValue(recipientAccountId);
        return this;
    }

    public TransferPage enterAmount(double amount) {
        amountInput.shouldBe(Condition.interactable).setValue(String.valueOf(amount));
        return this;
    }

    public TransferPage checkConfirmCheckbox() {
        confirmCheckbox.click();
        return this;
    }

    public TransferPage pressTransferButton(){
        transferButton.click();
        return this;
    }

    public TransferPage checkBalanceAccount(String accountId, double balance) {
        Selenide.refresh();

        String balanceShort = String.valueOf((int) balance); // 14900
        senderAccountSelector.shouldBe(Condition.visible).click();

        senderAccountSelector.$$("option")
                .findBy(Condition.matchText(accountId))
                .should(Condition.matchText(balanceShort));

        return this;
    }


}
