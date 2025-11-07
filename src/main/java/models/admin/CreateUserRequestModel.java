package models.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data //@Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@AllArgsConstructor //Создаёт конструктор со всеми полями
@NoArgsConstructor //Создаёт пустой (дефолтный) конструктор
@Builder //билдер
public class CreateUserRequestModel extends BaseModel {
    // models/ – классы данных для отправки и получения (POJOs).
    private String username;
    private String password;
    private String role;
}
