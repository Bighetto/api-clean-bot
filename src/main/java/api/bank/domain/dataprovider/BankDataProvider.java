package api.bank.domain.dataprovider;

import api.bank.domain.entity.BankEntity;

public interface BankDataProvider {

    BankEntity findByName(String bankName);
}
