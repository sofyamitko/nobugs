package models.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.accounts.AccountResponseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserProfileResponseModel {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountResponseModel> accounts;
}
