package api.security.auth.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import api.security.auth.app.converter.RecoveryTokenModelToEntityConverter;
import api.security.auth.app.model.RecoveryToken;
import api.security.auth.app.provider.RecoveryTokenProvider;
import api.security.auth.app.repository.RecoveryTokenRepository;
import api.security.auth.domain.entity.RecoveryTokenEntity;

@ExtendWith(MockitoExtension.class)
public class RecoveryTokenDataProviderTest {

    @Mock
    RecoveryTokenRepository tokenRepository;

    @Mock
    RecoveryTokenModelToEntityConverter tokenModelToEntityConverter;

    private RecoveryTokenDataProvider tokenProvider;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        tokenProvider = new RecoveryTokenProvider(tokenRepository, tokenModelToEntityConverter);
    }

    @Test
    void shouldGenerateNewTokenAfterDeletingOldOnes() {
        String userEmail = "teste@email.com";

        RecoveryToken newToken = new RecoveryToken(
            userEmail, 
            UUID.randomUUID().toString(), 
            LocalDateTime.now().plusMinutes(15)
        );

        RecoveryTokenEntity expectedEntity = new RecoveryTokenEntity(
            newToken.getUserEmail(), 
            newToken.getToken(), 
            newToken.getExpirationDateTime()
        );

        when(tokenModelToEntityConverter.convertToEntity(any(RecoveryToken.class))).thenReturn(expectedEntity);
        when(tokenRepository.save(any(RecoveryToken.class))).thenReturn(newToken);

        RecoveryTokenEntity result = tokenProvider.saveToken(newToken);

        verify(tokenRepository, times(1)).save(any(RecoveryToken.class));

        assertNotNull(result);
        assertEquals(userEmail, result.getUserEmail());
        assertNotNull(result.getToken());
        assertTrue(result.getExpirationDateTime().isAfter(LocalDateTime.now()));
    }


    @Test
    void shouldReturnTokenEntityWhenTokenExists() {
        String userEmail = "test@example.com";
        RecoveryToken token = new RecoveryToken();
        RecoveryTokenEntity tokenEntity = new RecoveryTokenEntity();
        
        when(tokenRepository.findByUserEmail(userEmail)).thenReturn(token);
        when(tokenModelToEntityConverter.convertToEntity(token)).thenReturn(tokenEntity);

        RecoveryTokenEntity result = tokenProvider.findTokenByUserEmail(userEmail);

        assertNotNull(result);
        verify(tokenRepository).findByUserEmail(userEmail);
        verify(tokenModelToEntityConverter).convertToEntity(token);
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        String userEmail = "test@example.com";
        when(tokenRepository.findByUserEmail(userEmail)).thenReturn(null);

        RecoveryTokenEntity result = tokenProvider.findTokenByUserEmail(userEmail);
        assertNull(result);
    }

    @Test
    void shouldDeleteTokenByUserEmail() {
        String userEmail = "test@example.com";
        
        tokenProvider.deleteTokenByUserEmail(userEmail);

        verify(tokenRepository).deleteByUserEmail(userEmail);
    }
}
