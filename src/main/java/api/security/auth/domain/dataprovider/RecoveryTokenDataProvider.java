package api.security.auth.domain.dataprovider;

import api.security.auth.app.model.RecoveryToken;
import api.security.auth.domain.entity.RecoveryTokenEntity;

public interface RecoveryTokenDataProvider {

    RecoveryTokenEntity saveToken(RecoveryToken model);

    RecoveryTokenEntity findTokenByUserEmail(String userEmail);

    RecoveryTokenEntity findByToken(String token);

    void deleteTokenByUserEmail(String userEmail);
}
