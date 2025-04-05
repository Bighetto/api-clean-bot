package api.security.auth.domain.usecase;

import api.security.auth.domain.entity.RecoveryTokenEntity;

public interface ValidadeExpirationTokenUseCase {
    RecoveryTokenEntity execute(String token);
}
