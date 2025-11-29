package api.asserts;

import org.assertj.core.api.SoftAssertions;

public class AccountBalanceAssert {
    private final AccountBalanceSnapshot snapshot;
    private final SoftAssertions softly;


    public AccountBalanceAssert(AccountBalanceSnapshot snapshot) {
        this.snapshot = snapshot;
        this.softly = new SoftAssertions();
    }

    public AccountBalanceAssert isIncreasedBy(double amount) {
        double before = snapshot.getBefore();
        double after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Balance should increase by %.2f", amount)
                .isEqualTo(before + amount);

        softly.assertAll();
        return this;
    }

    public AccountBalanceAssert isIncreasedSeveralTimesBy(double amount, int times) {
        double before = snapshot.getBefore();
        double after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Balance should increase by %.2f", amount)
                .isEqualTo(before + (amount*times));

        softly.assertAll();
        return this;
    }

    public AccountBalanceAssert isDecreasedBy(double amount) {
        double before = snapshot.getBefore();
        double after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Balance should decrease by %.2f", amount)
                .isEqualTo(before - amount);

        softly.assertAll();
        return this;
    }

    public AccountBalanceAssert isUnchanged() {
        double before = snapshot.getBefore();
        double after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Balance should remain unchanged")
                .isEqualTo(before);

        softly.assertAll();
        return this;
    }
}
