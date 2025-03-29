package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;

public interface BankUserDataProvider {

    List<BankUserEntity> findUsersBankByUser(UserLogin document);
}
