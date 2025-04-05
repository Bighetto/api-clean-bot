package config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class V8BankEnviroment {

    private String login;
    private String password;

    @Value("${api.v8.bank.audience}")
    private String audience;

    @Value("${api.v8.bank.clientid}")
    private String clientId;

    private String grantType = "password";
    private String scope = "offline_access";

    public V8BankEnviroment(String login, String password) {
        this.login = login;
        this.password = password;
    }
}