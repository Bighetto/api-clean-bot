package api.bank.app.entrypoints;

import java.util.List;

import org.springframework.http.ResponseEntity;

import api.bank.app.restmodel.BankUserRestModel;

public interface BankResource {

    ResponseEntity<List<BankUserRestModel>> findUsersBankByUser(String idUser);
    
}
