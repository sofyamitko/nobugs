package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private SelenideElement saveButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));
    private SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage enterName(String name) {
        newNameInput.shouldBe(Condition.interactable).setValue(name);
        // sendKeys надежнее в Selenoid
        return this;
    }

    public EditProfilePage saveChanges() {
        saveButton.click();
        return this;
    }
}
