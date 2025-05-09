package api.security.auth.app.provider;

import org.springframework.stereotype.Component;

import api.security.auth.app.converter.RecoveryTokenModelToEntityConverter;
import api.security.auth.app.model.RecoveryToken;
import api.security.auth.app.repository.RecoveryTokenRepository;
import api.security.auth.domain.dataprovider.RecoveryTokenDataProvider;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RecoveryTokenProvider implements RecoveryTokenDataProvider {

    private final RecoveryTokenRepository tokenRepository;

    private final RecoveryTokenModelToEntityConverter tokenModelToEntityConverter;

    @Override
    public RecoveryTokenEntity saveToken(RecoveryToken model) {
        return this.tokenModelToEntityConverter.convertToEntity
            (this.tokenRepository.save(model));
    }

    @Override
    public RecoveryTokenEntity findTokenByUserEmail(String userEmail) {
        RecoveryToken token = this.tokenRepository.findByUserEmail(userEmail);

        if(token == null) {
            return null;
        }

        return this.tokenModelToEntityConverter.convertToEntity(token);
    }

    @Override
    public RecoveryTokenEntity findByToken(String token) {
        RecoveryToken recoveryToken = this.tokenRepository.findByToken(token);

        if(recoveryToken == null) {
            return null;
        }

        return this.tokenModelToEntityConverter.convertToEntity(recoveryToken);
    }

    @Override
    public void deleteTokenByUserEmail(String userEmail) {
        this.tokenRepository.deleteByUserEmail(userEmail);
    }

}
