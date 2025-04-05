package api.bank.domain.service;

import org.springframework.stereotype.Service;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import config.V8BankEnviroment;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ValidateUserBankV8Service implements ValidateUserBankV8UseCase {

    @Override
    public Boolean execute(UploadBankUserRequestRestModel requestRestModel) {

        V8BankEnviroment data = new V8BankEnviroment(requestRestModel.getLogin(), requestRestModel.getPassword());

        try {
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return null;
    }

}
