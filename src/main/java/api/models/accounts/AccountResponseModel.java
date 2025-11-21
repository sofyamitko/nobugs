package api.models.accounts;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data //@Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@AllArgsConstructor //Создаёт конструктор со всеми полями
@NoArgsConstructor //Создаёт пустой (дефолтный) конструктор
@Builder //билдер
public class AccountResponseModel extends BaseModel {
    private Integer id;
    private String accountNumber;
    private Double balance;
    private List<TransactionResponseModel> transactions;
}