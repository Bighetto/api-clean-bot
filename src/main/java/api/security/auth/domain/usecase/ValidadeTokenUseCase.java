package api.security.auth.domain.usecase;

import api.security.auth.domain.entity.RecoveryTokenEntity;

public interface ValidadeTokenUseCase {
    RecoveryTokenEntity execute(String token);
}
