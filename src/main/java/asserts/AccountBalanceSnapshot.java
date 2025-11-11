package asserts;

import requests.steps.UserSteps;

public class AccountBalanceSnapshot {
    private final String username;
    private final String password;
    private final int accountId;
    private final double balanceBefore;

    private AccountBalanceSnapshot(String username, String password, int accountId, double balanceBefore) {
        this.username = username;
        this.password = password;
        this.accountId = accountId;
        this.balanceBefore = balanceBefore;
    }

    public static AccountBalanceSnapshot of(String username, String password, int accountId) {
        double balance = UserSteps.checkAccountBalance(username, password, accountId);
        return new AccountBalanceSnapshot(username, password, accountId, balance);
    }

    public double getBefore() {
        return balanceBefore;
    }

    public double getAfter() {
        return UserSteps.checkAccountBalance(username, password, accountId);
    }

    public AccountBalanceAssert assertThat() {
        return new AccountBalanceAssert(this);
    }
}
