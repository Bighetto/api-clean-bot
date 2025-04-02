package api.security.auth.app.provider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import api.security.auth.app.converter.TokenModelToEntityConverter;
import api.security.auth.app.model.Token;
import api.security.auth.app.repository.TokenRepository;
import api.security.auth.domain.dataprovider.TokenDataProvider;
import api.security.auth.domain.entity.TokenEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TokenProvider implements TokenDataProvider {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenModelToEntityConverter tokenModelToEntityConverter;

    @Override
    public TokenEntity generateToken(String userEmail) {

        List<Token> token = this.tokenRepository.findAllByUserEmail(userEmail);

        if(token.size() != 0) {
            this.tokenRepository.deleteByUserEmail(userEmail);
        }

        Token resetToken = new Token(
            userEmail, 
            UUID.randomUUID().toString(), 
            LocalDateTime.now().plusMinutes(15)
        );

        return this.tokenModelToEntityConverter.convertToEntity
            (this.tokenRepository.save(resetToken));
    }
}
