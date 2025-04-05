package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.app.model.Bank;
import api.bank.app.repository.BankRepository;
import api.bank.domain.dataprovider.BankDataProvider;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankProvider implements BankDataProvider{

    private final BankRepository bankRepository;

    @Override
    public Bank findByName(String bankName) {
        return this.bankRepository.findByName(bankName);
    }

}
