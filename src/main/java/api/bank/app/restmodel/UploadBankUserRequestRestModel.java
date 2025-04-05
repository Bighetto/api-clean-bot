package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadBankUserRequestRestModel {

    private String login;
    private String password;
    private String nickname;
    private String bankName;
    private String userEmail;
}
