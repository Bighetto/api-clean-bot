package api.security.auth.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.security.auth.app.model.RecoveryToken;
import api.security.auth.domain.dataprovider.RecoveryTokenDataProvider;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.service.GenerateRecoveryTokenService;

public class GenerateRecoveryTokenUseCaseTest {

    @Mock
    RecoveryTokenDataProvider tokenDataProvider;

    GenerateRecoveryTokenUseCase generateRecoveryTokenUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        generateRecoveryTokenUseCase = new GenerateRecoveryTokenService(tokenDataProvider);
    }

    @Test
    void shouldGenerateNewTokenWhenNoExistingToken() {
        String userEmail = "test@email.com";
        when(tokenDataProvider.findTokenByUserEmail(userEmail)).thenReturn(null);
        
        RecoveryTokenEntity tokenEntity = new RecoveryTokenEntity();
        tokenEntity.setUserEmail(userEmail);
        when(tokenDataProvider.saveToken(any(RecoveryToken.class))).thenReturn(tokenEntity);

        RecoveryTokenEntity result = generateRecoveryTokenUseCase.execute(userEmail);

        assertNotNull(result);
        assertEquals(userEmail, result.getUserEmail());
    }

    @Test
    void shouldReplaceExistingToken() {
        String userEmail = "test@example.com";
        RecoveryTokenEntity existingToken = new RecoveryTokenEntity();
        existingToken.setUserEmail(userEmail);
        
        when(tokenDataProvider.findTokenByUserEmail(userEmail)).thenReturn(existingToken);
        when(tokenDataProvider.saveToken(any(RecoveryToken.class))).thenReturn(existingToken);

        RecoveryTokenEntity result = generateRecoveryTokenUseCase.execute(userEmail);

        verify(tokenDataProvider).deleteTokenByUserEmail(userEmail);
        assertNotNull(result);
        assertEquals(userEmail, result.getUserEmail());
    }
}
