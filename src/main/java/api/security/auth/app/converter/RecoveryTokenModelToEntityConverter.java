package api.security.auth.app.converter;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.RecoveryToken;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.utils.abstractClasses.ConvertCase;

@Component
public class RecoveryTokenModelToEntityConverter extends ConvertCase<RecoveryTokenEntity, RecoveryToken> {

    @Override
    public RecoveryToken convertToModel(RecoveryTokenEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public RecoveryTokenEntity convertToEntity(RecoveryToken model) {
        return new RecoveryTokenEntity().builder()
            .userEmail(model.getUserEmail())
            .token(model.getToken())
            .expirationDateTime(model.getExpirationDateTime())
        .build();
    }

}
