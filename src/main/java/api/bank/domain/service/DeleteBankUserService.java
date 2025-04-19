package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.exception.BankUserNotFoundException;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.usecase.DeleteBankUserUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DeleteBankUserService implements DeleteBankUserUseCase {

    private final BankUserDataProvider bankUserDataProvider;

    @Override
    public void execute(String bankUserId) {
        Integer rowsAffected = this.bankUserDataProvider.deleteBankUserById(bankUserId);

        if(rowsAffected == 0) {
            throw new BankUserNotFoundException();
        }
    }
}
