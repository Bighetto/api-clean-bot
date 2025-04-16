package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class V8BankRequestDTO {

    private String username;
    private String password;
    private String audience;
    private String clientId;
    private String grantType;
    private String scope;
}
