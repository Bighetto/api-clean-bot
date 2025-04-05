package api.security.auth.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.security.auth.domain.dataprovider.RecoveryTokenDataProvider;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.service.ValidateRecoveryTokenService;

public class ValidateTokenUseCase {

    @Mock
    private RecoveryTokenDataProvider recoveryTokenDataProvider;

    ValidadeExpirationTokenUseCase validateTokenUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validateTokenUseCase = new ValidateRecoveryTokenService(recoveryTokenDataProvider);
    }    

    @Test
    void shouldReturnRecoveryTokenEntity() {
        String token = "testToken";

        RecoveryTokenEntity recoveryTokenEntity = new RecoveryTokenEntity();
        recoveryTokenEntity.setToken(token);
        recoveryTokenEntity.setUserEmail("testUserEmail");

        when(this.recoveryTokenDataProvider.findByToken(token)).thenReturn(recoveryTokenEntity);

        RecoveryTokenEntity validateRecoveryTokenEntity = validateTokenUseCase.execute(token);

        assertTrue(validateRecoveryTokenEntity != null);
        verify(recoveryTokenDataProvider, times(1)).deleteTokenByUserEmail(anyString());
    }
}
