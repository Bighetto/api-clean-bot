package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.app.converter.BankModelToEntityConverter;
import api.bank.app.repository.BankRepository;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.entity.BankEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankProvider implements BankDataProvider{

    private final BankRepository bankRepository;
    private final BankModelToEntityConverter bankModelToEntityConverter;

    @Override
    public BankEntity findByName(String bankName) {
        return this.bankModelToEntityConverter.convertToEntity(this.bankRepository.findByName(bankName));
    }

}
