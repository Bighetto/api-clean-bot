package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.BankUser;
import api.bank.domain.entity.BankUserEntity;
import api.utils.abstractClasses.ConvertCase;

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
        .userLoginId(model.getUser().getDocument())
        .bankId(model.getBank().getId())
        .login(model.getLogin())
        .password(model.getPassword())
        .nickname(model.getNickname())
        .build();
    }

}
