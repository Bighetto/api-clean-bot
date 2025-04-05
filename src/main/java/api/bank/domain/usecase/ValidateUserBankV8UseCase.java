package api.bank.domain.usecase;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;

public interface ValidateUserBankV8UseCase {
    Boolean execute(UploadBankUserRequestRestModel requestRestModel);
}
