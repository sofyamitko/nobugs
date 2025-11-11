package models.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserProfileResponseModel extends BaseModel {
    private GetUserProfileResponseModel customer;
    private String message;
}