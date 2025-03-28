package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.domain.entity.BankUserEntity;

public interface BankUserDataProvider {

    List<BankUserEntity> findUsersBankByUserDocument(String document);
}
