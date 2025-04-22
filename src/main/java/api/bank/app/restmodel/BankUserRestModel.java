package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankUserRestModel {

    private String id;
    private String username;
    private String bankName;
    private String nickname;
}
