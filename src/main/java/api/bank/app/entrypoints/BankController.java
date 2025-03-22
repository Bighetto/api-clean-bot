package api.bank.app.entrypoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.bank.app.restmodel.BankRestModel;
import lombok.AllArgsConstructor;

@RequestMapping(value = "/bank")
@AllArgsConstructor
@CrossOrigin
@RestController
public class BankController implements BankResource {
    
    
    @Override
    public ResponseEntity<BankRestModel> findUsersBankByUser(String idUser) {


        

        return ResponseEntity.badRequest().build();
    }


    
}
