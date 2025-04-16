package api.bank.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {

    Bank findByName(String bankName);
    
}
