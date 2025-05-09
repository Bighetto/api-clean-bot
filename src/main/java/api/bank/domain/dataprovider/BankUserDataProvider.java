package api.bank.domain.dataprovider;

import java.util.List;
import java.util.Optional;

import api.bank.app.model.BankUser;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;

public interface BankUserDataProvider {

    void uploadBankUser(BankUser bankUser);

    List<BankUserEntity> findUsersBankByUser(UserLogin document);

    Boolean existsByLoginAndBankId(String login, String bankId);

    Integer deleteBankUserById(String id);

    Optional<BankUser> findBankUserById(String id);
}
