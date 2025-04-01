package api.security.auth.domain.usecase;

import api.security.auth.domain.entity.TokenEntity;

public interface GenerateTokenUseCase {
    TokenEntity execute(String userEmail);
}
