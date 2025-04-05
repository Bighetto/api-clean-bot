package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.usecase.UploadBankUserUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UploadBankUserService implements UploadBankUserUseCase {

    private final BankUserDataProvider bankUserDataProvider;

    @Override
    public void execute(UploadBankUserRequestRestModel restModel) {
        this.bankUserDataProvider.uploadBankUser(restModel);
    }

    
}
