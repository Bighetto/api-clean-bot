package api.security.auth.app.converter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserModelToEntityConverter extends ConvertCase<UserEntity, UserLogin>{

    private final UserBankModelToEntityConverter userBankConverter;

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
            .userBanks(model.getUserBanks().stream().map(bankUser -> userBankConverter.convertToEntity(bankUser)).collect(Collectors.toList()))
            .build();
    }
    
}
