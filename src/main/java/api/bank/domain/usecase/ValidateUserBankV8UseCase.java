package api.bank.domain.usecase;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;

public interface ValidateUserBankV8UseCase {
    void execute(UploadBankUserRequestRestModel requestRestModel) throws Exception;
}
