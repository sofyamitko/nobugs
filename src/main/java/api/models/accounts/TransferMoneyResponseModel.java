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
public class TransferMoneyResponseModel extends BaseModel {
    private Integer receiverAccountId;
    private Double amount;
    private String message;
    private Integer senderAccountId;
}