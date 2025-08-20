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
public class HubBankConfig {

    @Value("${api.hub.bank.url.getToken}")
    private String hubBankURLGetToken;

    @Value("${api.hub.bank.url.simulate}")
    private String hubBankURLSimulate;

    @Value("${api.hub.bank.lojaId}")
    private String lojaId;
    
    @Value("${api.hub.bank.grantType}")
    private String grantType;
}
