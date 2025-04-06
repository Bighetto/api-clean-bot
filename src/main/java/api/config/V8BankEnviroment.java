package api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Component
public class V8BankEnviroment {

    private String username;
    private String password;

    @Value("${api.v8.bank.audience}")
    private String audience;

    @Value("${api.v8.bank.clientid}")
    private String clientId;

    private String grantType = "password";
    private String scope = "offline_access";

}