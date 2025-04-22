package api.bank.app.entrypoints;

import java.util.List;

import org.springframework.http.ResponseEntity;

import api.bank.app.restmodel.BankUserRestModel;
import api.bank.app.restmodel.UpdateBankUserNicknameRequestDTO;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;

public interface BankResource {

    ResponseEntity<String> uploadBankUser(UploadBankUserRequestRestModel restModel);

    ResponseEntity<List<BankUserRestModel>> findUsersBankByUser(String idUser);
    
    ResponseEntity<String> updateBankUserNickname(UpdateBankUserNicknameRequestDTO dto);
}
