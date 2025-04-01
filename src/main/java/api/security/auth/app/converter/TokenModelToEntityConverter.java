package api.security.auth.app.converter;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.Token;
import api.security.auth.domain.entity.TokenEntity;
import api.utils.abstractClasses.ConvertCase;

@Component
public class TokenModelToEntityConverter extends ConvertCase<TokenEntity, Token> {

    @Override
    public Token convertToModel(TokenEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public TokenEntity convertToEntity(Token model) {
        return new TokenEntity().builder()
            .id(model.getId())
            .userId(model.getUser()
            .getDocument())
            .token(model.getToken())
            .createdAt(model.getCreatedAt())
        .build();
    }

}
