package api.models.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;
import api.models.accounts.AccountResponseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserProfileResponseModel extends BaseModel {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountResponseModel> accounts;
}