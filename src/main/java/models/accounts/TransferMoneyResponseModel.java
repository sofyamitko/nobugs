package models.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyResponseModel {
    private Integer receiverAccountId;
    private Double amount;
    private String message;
    private Integer senderAccountId;
}
