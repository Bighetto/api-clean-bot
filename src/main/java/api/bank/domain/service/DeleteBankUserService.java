package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.exception.BankUserNotFoundException;
import api.bank.app.exception.InvalidBankUserIdException;
import api.bank.app.restmodel.DeleteBankUserRequestDTO;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.usecase.DeleteBankUserUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DeleteBankUserService implements DeleteBankUserUseCase {

    private final BankUserDataProvider bankUserDataProvider;

    @Override
    public void execute(DeleteBankUserRequestDTO dto) {
        if (dto.getBankUserId() == null || dto.getBankUserId().isBlank()) {
            throw new InvalidBankUserIdException();
        }

        Integer rowsAffected = this.bankUserDataProvider.deleteBankUserById(dto.getBankUserId());

        if(rowsAffected == 0) {
            throw new BankUserNotFoundException();
        }
    }
}
