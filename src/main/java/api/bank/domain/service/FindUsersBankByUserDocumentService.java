package api.bank.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;

@Service
public class FindUsersBankByUserDocumentService implements FindUsersBankByUserDocumentUseCase{

    @Autowired
    BankUserDataProvider bankUserDataProvider;

    @Override
    public List<BankUserEntity> execute(String document) {
        return this.bankUserDataProvider.findUsersBankByUserDocument(document);
    }

}
