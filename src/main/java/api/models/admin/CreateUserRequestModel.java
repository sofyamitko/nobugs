package api.models.admin;

import api.configs.Config;
import api.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data //@Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@AllArgsConstructor //Создаёт конструктор со всеми полями
@NoArgsConstructor //Создаёт пустой (дефолтный) конструктор
@Builder //билдер
public class CreateUserRequestModel extends BaseModel {
    // models/ – классы данных для отправки и получения (POJOs).
    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private String role;

    public static CreateUserRequestModel getAdmin(){
        return CreateUserRequestModel.builder()
                .username(Config.getProperty("admin.username"))
                .password(Config.getProperty("admin.password"))
                .build();
    }
}