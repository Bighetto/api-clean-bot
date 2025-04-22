package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.app.converter.BankModelToEntityConverter;
import api.bank.app.exception.BankNotFoundException;
import api.bank.app.model.Bank;
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
        Bank bank = this.bankRepository.findByName(bankName);

        if (bank == null) {
            throw new BankNotFoundException("Bank not found");
        }

        return this.bankModelToEntityConverter.convertToEntity(bank);
    }

}
