package api.bank.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.BankUser;

@Repository
public interface BankUserRepository extends JpaRepository<BankUser, String> {
    List<BankUser> findByUserDocument(String id);
}
