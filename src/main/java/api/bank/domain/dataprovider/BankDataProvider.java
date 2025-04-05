package api.bank.domain.dataprovider;

import api.bank.app.model.Bank;

public interface BankDataProvider {

    Bank findByName(String bankName);
}
