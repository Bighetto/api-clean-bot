package api.security.auth.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.RecoveryToken;
import api.security.auth.domain.dataprovider.RecoveryTokenDataProvider;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.usecase.GenerateRecoveryTokenUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GenerateRecoveryTokenService implements GenerateRecoveryTokenUseCase {

    private final RecoveryTokenDataProvider tokenDataProvider;

    @Override
    public RecoveryTokenEntity execute(String userEmail) {
        RecoveryTokenEntity token = this.tokenDataProvider.findTokenByUserEmail(userEmail);

        if(token != null) {
            this.tokenDataProvider.deleteTokenByUserEmail(userEmail);
        }

        RecoveryToken model = new RecoveryToken(
            userEmail, 
            UUID.randomUUID().toString(), 
            LocalDateTime.now().plusMinutes(15)
        );

        return this.tokenDataProvider.saveToken(model);
    }
}
