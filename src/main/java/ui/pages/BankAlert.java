package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: %s"),
    SUCCESSFULLY_DEPOSITED_TO_ACCOUNT("✅ Successfully deposited $%s to account %s!"),
    SELECT_ACCOUNT("❌ Please select an account."),
    ENTER_VALID_AMOUNT("❌ Please enter a valid amount."),
    DEPOSIT_LESS_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    ENTER_VALID_NAME("❌ Please enter a valid name."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred $%s to account %s!"),
    TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01("❌ Error: Transfer amount must be at least 0.01"),
    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS("❌ Error: Invalid transfer: insufficient funds or invalid accounts");

    private final String template;

    BankAlert(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }

    public String getMessage() {
        return template;
    }
}