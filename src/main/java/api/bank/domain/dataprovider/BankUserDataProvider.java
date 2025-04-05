package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;

public interface BankUserDataProvider {

    void uploadBankUser(UploadBankUserRequestRestModel requestRestModel);

    List<BankUserEntity> findUsersBankByUser(UserLogin document);

}
