package models.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserResponseModel extends BaseModel {
    // models/ – классы данных для отправки и получения (POJOs).

    private String username;
    private String role;
}
