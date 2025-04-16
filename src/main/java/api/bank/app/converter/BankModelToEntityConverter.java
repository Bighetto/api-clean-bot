package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.Bank;
import api.bank.domain.entity.BankEntity;
import api.utils.abstractClasses.ConvertCase;

@Component
public class BankModelToEntityConverter extends ConvertCase<BankEntity, Bank> {

    @Override
    public Bank convertToModel(BankEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public BankEntity convertToEntity(Bank model) {
        return BankEntity.builder()
            .name(model.getName())
            .id(model.getId())
        .build();
    }

}
