package api.security.auth.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.security.auth.domain.dataprovider.TokenDataProvider;
import api.security.auth.domain.entity.TokenEntity;
import api.security.auth.domain.usecase.GenerateTokenUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GenerateTokenService implements GenerateTokenUseCase {

    @Autowired
    TokenDataProvider tokenDataProvider;

    @Override
    public TokenEntity execute(String userEmail) {
        return this.tokenDataProvider.generateToken(userEmail);
    }
}
