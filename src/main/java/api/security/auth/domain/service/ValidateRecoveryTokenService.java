package api.security.auth.domain.service;

import org.springframework.stereotype.Component;

import api.security.auth.domain.dataprovider.RecoveryTokenDataProvider;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.usecase.ValidadeExpirationTokenUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ValidateRecoveryTokenService implements ValidadeExpirationTokenUseCase {

    private final RecoveryTokenDataProvider recoveryTokenDataProvider;

    @Override
    public RecoveryTokenEntity execute(String token) {
        RecoveryTokenEntity recoveryTokenEntity = this.recoveryTokenDataProvider.findByToken(token);

        this.recoveryTokenDataProvider.deleteTokenByUserEmail(recoveryTokenEntity.getUserEmail());

        return recoveryTokenEntity;
    }
}
