package api.requests.skelethon;

import api.models.accounts.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import api.models.BaseModel;
import api.models.admin.CreateUserRequestModel;
import api.models.admin.CreateUserResponseModel;
import api.models.authentication.LoginUserRequestModel;
import api.models.authentication.LoginUserResponseModel;
import api.models.customer.GetUserProfileResponseModel;
import api.models.customer.UpdateUserProfileRequestModel;
import api.models.customer.UpdateUserProfileResponseModel;

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
