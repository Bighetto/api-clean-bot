package api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Configuration
public class V8BankConfig {

    @Value("${api.v8.bank.url}")
    private String v8BankURL;

    @Value("${api.v8.bank.audience}")
    private String audience;

    @Value("${api.v8.bank.clientid}")
    private String clientId;

    private String grantType = "password";
    private String scope = "offline_access";

}