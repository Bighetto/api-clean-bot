package api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {
    
    @Value("${WEBHOOK_SECRET_TOKEN}")
    private String webhookSecretToken;

    @Value("${ACCESS_TOKEN_FB}")
    private String accessTokenFb;

    @Value("${PIXEL_ID_FB}")
    private String pixelIdFb;

    @Bean
    public String webhookToken() {
        return webhookSecretToken;
    }
    @Bean
    public String accessTokenFb() {
        return accessTokenFb;
    }
    @Bean
    public String pixelIdFb() {
        return pixelIdFb;
    }
}
