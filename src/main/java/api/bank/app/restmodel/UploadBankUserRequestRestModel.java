package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadBankUserRequestRestModel {

    private String login;
    private String password;
    private String nickname;
    private String bankName;
    private String userEmail;
}
