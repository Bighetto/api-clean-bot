package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBankRestModel {

    private String name;
    private String nickname;
    private String password;
    private String bankName;
    
}
