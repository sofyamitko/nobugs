package api.models.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseModel extends BaseModel {
    private Integer id;
    private Double amount;
    private String type;
    private String timestamp;
    private Integer relatedAccountId;
}