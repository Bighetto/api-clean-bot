package api.bank.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankUserEntity {

    private String id;
    private String userLoginId;
    private String bankName;
    private String login;
    private String password;
    private String nickname;
}
