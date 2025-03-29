package api.bank.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.entity.UserEntity;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FindUsersBankByUserDocumentService implements FindUsersBankByUserDocumentUseCase{

    private final AuthDataProvider authDataProvider;

    @Override
    public List<BankUserEntity> execute(String email) {

        UserEntity userAuth = this.authDataProvider.findByEmail(email);

        return userAuth.getUserBanks();
    }

}
