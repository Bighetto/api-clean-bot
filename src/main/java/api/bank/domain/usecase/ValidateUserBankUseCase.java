package api.bank.domain.usecase;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;

public interface ValidateUserBankUseCase {
    void execute(UploadBankUserRequestRestModel restModel);
}
