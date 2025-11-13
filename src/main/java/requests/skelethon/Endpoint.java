package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.BaseModel;
import models.accounts.*;
import models.admin.CreateUserRequestModel;
import models.admin.CreateUserResponseModel;
import models.authentication.LoginUserRequestModel;
import models.authentication.LoginUserResponseModel;
import models.customer.GetUserProfileResponseModel;
import models.customer.UpdateUserProfileRequestModel;
import models.customer.UpdateUserProfileResponseModel;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequestModel.class,
            CreateUserResponseModel.class
    ),
    LOGIN(
            "/auth/login",
            LoginUserRequestModel.class,
            LoginUserResponseModel.class
    ),
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountResponseModel.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            null,
            AccountResponseModel.class
    ),
    DEPOSIT(
            "/accounts/deposit",
            AccountRequestModel.class,
            AccountResponseModel.class
    ),
    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequestModel.class,
            TransferMoneyResponseModel.class
    ),
    UPDATE_PROFILE(
            "/customer/profile",
            UpdateUserProfileRequestModel.class,
            UpdateUserProfileResponseModel.class
    ),
   GET_PROFILE(
            "/customer/profile",
           null,
            GetUserProfileResponseModel.class
    ),
    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            null,
            TransactionResponseModel.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
