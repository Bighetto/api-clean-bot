package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.BankUserRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.domain.utils.abstractClasses.ConvertCase;

@Component
public class BankUserEntityToRestModelConverter extends ConvertCase<BankUserEntity, BankUserRestModel> {

    @Override
    public BankUserRestModel convertToModel(BankUserEntity entity) {
        return BankUserRestModel.builder()
        .username(entity.getLogin())
        .bankName(entity.getBankName())
        .nickname(entity.getNickname())
        .build();
    }

    @Override
    public BankUserEntity convertToEntity(BankUserRestModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToEntity'");
    }

}
