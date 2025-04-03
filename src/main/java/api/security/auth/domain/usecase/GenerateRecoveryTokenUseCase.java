package api.security.auth.domain.usecase;

import api.security.auth.domain.entity.RecoveryTokenEntity;

public interface GenerateRecoveryTokenUseCase {
    RecoveryTokenEntity execute(String userEmail);
}
