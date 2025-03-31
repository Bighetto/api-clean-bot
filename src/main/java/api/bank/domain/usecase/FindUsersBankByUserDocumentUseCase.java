package api.bank.domain.usecase;

import java.util.List;

import api.bank.domain.entity.BankUserEntity;

public interface FindUsersBankByUserDocumentUseCase {

    List<BankUserEntity> execute(String email);
}
