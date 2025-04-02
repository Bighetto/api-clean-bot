package api.security.auth.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import api.security.auth.app.converter.TokenModelToEntityConverter;
import api.security.auth.app.model.Token;
import api.security.auth.app.provider.TokenProvider;
import api.security.auth.app.repository.TokenRepository;
import api.security.auth.domain.entity.TokenEntity;

@ExtendWith(MockitoExtension.class)
public class TokenDataProviderTest {

    @Mock
    TokenRepository tokenRepository;

    @Mock
    TokenModelToEntityConverter tokenModelToEntityConverter;

    private TokenDataProvider tokenProvider;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        tokenProvider = new TokenProvider(tokenRepository, tokenModelToEntityConverter);
    }

    @Test
    void shouldGenerateNewTokenAfterDeletingOldOnes() {
        String userEmail = "teste@email.com";
        List<Token> existingTokens = List.of(new Token(
            userEmail, 
            "oldToken", 
            LocalDateTime.now().plusMinutes(10))
        );

        Token newToken = new Token(
            userEmail, 
            UUID.randomUUID().toString(), 
            LocalDateTime.now().plusMinutes(15)
        );

        
        TokenEntity expectedEntity = new TokenEntity(
            newToken.getUserEmail(), 
            newToken.getToken(), 
            newToken.getExpirationDateTime()
        );

        when(tokenRepository.findAllByUserEmail(userEmail)).thenReturn(existingTokens);
        when(tokenModelToEntityConverter.convertToEntity(any(Token.class))).thenReturn(expectedEntity);
        when(tokenRepository.save(any(Token.class))).thenReturn(newToken);

        TokenEntity result = tokenProvider.generateToken(userEmail);

        verify(tokenRepository, times(1)).deleteByUserEmail(userEmail);
        verify(tokenRepository, times(1)).save(any(Token.class));

        assertNotNull(result);
        assertEquals(userEmail, result.getUserEmail());
        assertNotNull(result.getToken());
        assertTrue(result.getExpirationDateTime().isAfter(LocalDateTime.now()));
    }

}
