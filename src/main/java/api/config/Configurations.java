package api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {
    
    @Value("${WEBHOOK_SECRET_TOKEN}")
    private String webhookSecretToken;

    @Bean
    public String webhookToken() {
        return webhookSecretToken;
    }
}
