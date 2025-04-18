package api.bank.domain.usecase;

import api.bank.app.restmodel.DeleteBankUserRequestDTO;

public interface DeleteBankUserUseCase {
    void execute(DeleteBankUserRequestDTO dto);
}
