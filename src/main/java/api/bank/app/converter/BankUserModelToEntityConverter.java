package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.BankUser;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.domain.utils.abstractClasses.ConvertCase;

@Component
public class BankUserModelToEntityConverter extends ConvertCase<BankUserEntity, BankUser> {

    @Override
    public BankUser convertToModel(BankUserEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public BankUserEntity convertToEntity(BankUser model) {
        return BankUserEntity.builder()
        .id(model.getId())
        .user(model.getUser())
        .bankName(model.getBankName())
        .login(model.getLogin())
        .password(model.getPassword())
        .nickname(model.getNickname())
        .build();
    }

}
