package api.bank.app.entrypoints;

import org.springframework.http.ResponseEntity;

import api.bank.app.restmodel.BankRestModel;

public interface BankResource {

    ResponseEntity<BankRestModel> findUsersBankByUser(String idUser);

    
    
}
