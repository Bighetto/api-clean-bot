package api.bank.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import api.bank.app.model.Bank;
import api.bank.app.model.BankUser;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankEntity;
import api.bank.domain.usecase.UploadBankUserUseCase;
import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.entity.UserEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UploadBankUserService implements UploadBankUserUseCase {

    private final BankUserDataProvider bankUserDataProvider;
    private final AuthDataProvider authDataProvider;
    private final BankDataProvider bankDataProvider;

    @Override
    public void execute(UploadBankUserRequestRestModel restModel) {
        UserEntity userEntity = this.authDataProvider.findByEmail(restModel.getUserEmail());
        BankEntity bankEntity = this.bankDataProvider.findByName(restModel.getBankName());

        UserLogin userLogin = new UserLogin();
        userLogin.setDocument(userEntity.getDocument());

        Bank bank = new Bank();
        bank.setId(bankEntity.getId());

        BankUser bankUser = new BankUser();
        bankUser.setId(UUID.randomUUID().toString());
        bankUser.setUser(userLogin);
        bankUser.setBank(bank);
        bankUser.setLogin(restModel.getLogin());
        bankUser.setPassword(restModel.getPassword());
        bankUser.setNickname(restModel.getNickname());
        
        this.bankUserDataProvider.uploadBankUser(bankUser);
    }
}
