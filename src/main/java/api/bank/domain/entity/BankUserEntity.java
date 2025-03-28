package api.bank.domain.entity;

import api.security.auth.app.model.UserLogin;
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
    private UserLogin user;
    private String bankName;
    private String login;
    private String password;
    private String nickname;
}
