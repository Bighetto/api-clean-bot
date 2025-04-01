package api.security.auth.app.provider;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import api.security.auth.app.converter.TokenModelToEntityConverter;
import api.security.auth.app.model.Token;
import api.security.auth.app.model.UserLogin;
import api.security.auth.app.repository.TokenRepository;
import api.security.auth.app.repository.UserRepository;
import api.security.auth.domain.dataprovider.TokenDataProvider;
import api.security.auth.domain.entity.TokenEntity;

@Component
public class TokenProvider implements TokenDataProvider {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenModelToEntityConverter tokenModelToEntityConverter;

    @Override
    public TokenEntity generateToken(String userEmail) {
        Optional<UserLogin> user = Optional.ofNullable(this.userRepository.findByEmail(userEmail));

        if (user.isEmpty()) {
            throw new RuntimeException();
        }

        String token = UUID.randomUUID().toString();
        Token resetToken = new Token(user.get(), token, false);

        return this.tokenModelToEntityConverter.convertToEntity
            (this.tokenRepository.save(resetToken));
    }

}
