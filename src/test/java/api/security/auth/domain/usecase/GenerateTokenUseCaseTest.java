package api.security.auth.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.security.auth.domain.dataprovider.TokenDataProvider;
import api.security.auth.domain.entity.TokenEntity;
import api.security.auth.domain.service.GenerateTokenService;

public class GenerateTokenUseCaseTest {

    @Mock
    TokenDataProvider tokenDataProvider;

    GenerateTokenUseCase generateTokenUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        generateTokenUseCase = new GenerateTokenService(tokenDataProvider);
    }

    @Test
    void shouldReturnTokenEntityWithSucessful() {
        String userEmail = "TestUserEmail";

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserEmail(userEmail);
        
        when(this.tokenDataProvider.generateToken(tokenEntity.getUserEmail())).thenReturn(tokenEntity);

        TokenEntity result = this.generateTokenUseCase.execute(userEmail);

        assertEquals(userEmail, result.getUserEmail());
    }
}
