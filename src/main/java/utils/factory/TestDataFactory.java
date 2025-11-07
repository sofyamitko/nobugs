package utils.factory;

import generators.RandomData;
import models.UserRole;
import models.admin.CreateUserRequestModel;
import utils.AccountBalanceUtils;
import utils.UserUtils;

public class TestDataFactory {

    private final AccountBalanceUtils accountBalanceUtils = new AccountBalanceUtils();
    private final UserUtils userUtils = new UserUtils();

    public TestUserContext createUserWithAccounts() {

        CreateUserRequestModel userRequest = buildRandomUserRequest();
        userUtils.createUser(userRequest);
        int acc1 = accountBalanceUtils.createAccount(userRequest.getUsername(), userRequest.getPassword());
        int acc2 = accountBalanceUtils.createAccount(userRequest.getUsername(), userRequest.getPassword());

        return TestUserContext.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .firstAccountId(acc1)
                .secondAccountId(acc2)
                .build();
    }

    public TestUserContext createUserWithAccount() {

        CreateUserRequestModel userRequest = buildRandomUserRequest();
        userUtils.createUser(userRequest);
        int acc1 = accountBalanceUtils.createAccount(userRequest.getUsername(), userRequest.getPassword());

        return TestUserContext.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .firstAccountId(acc1)
                .build();
    }

    public TestUserContext createUser() {

        CreateUserRequestModel userRequest = buildRandomUserRequest();
        userUtils.createUser(userRequest);

        return TestUserContext.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();
    }

    private CreateUserRequestModel buildRandomUserRequest() {
        return CreateUserRequestModel.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();
    }
}
