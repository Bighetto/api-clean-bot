package api.security.auth.app.converter;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.utils.abstractClasses.ConvertCase;

@Component
public class UserModelToEntityConverter extends ConvertCase<UserEntity, UserLogin>{

    @Override
    public UserLogin convertToModel(UserEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public UserEntity convertToEntity(UserLogin model) {

        return UserEntity
            .builder()
            .document(model.getDocument())
            .name(model.getName())
            .email(model.getEmail())
            .registerDate(model.getRegisterDate())
            .phoneNumber(model.getPhoneNumber())
            .tipo(model.getTipo())
            .build();
    }
    
}
