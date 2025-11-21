package api.models.admin;

import api.models.accounts.AccountResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponseModel extends BaseModel {
    // models/ – классы данных для отправки и получения (POJOs).
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountResponseModel> accounts;
}