package api.bank.domain.usecase;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;

public interface UploadBankUserUseCase {
    void execute(UploadBankUserRequestRestModel restModel);
}
