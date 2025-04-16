package api.bank.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.BankUser;
import api.security.auth.app.model.UserLogin;

@Repository
public interface BankUserRepository extends JpaRepository<BankUser, String> {
    List<BankUser> findByUserDocument(String id);

    List<BankUser> findByUser(UserLogin user);

    Boolean existsByLoginAndBankId(String login, String bankId);
}
