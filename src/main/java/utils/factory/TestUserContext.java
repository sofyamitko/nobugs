package utils.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestUserContext {
    private String username;
    private String password;
    private Integer firstAccountId;
    private Integer secondAccountId;
}