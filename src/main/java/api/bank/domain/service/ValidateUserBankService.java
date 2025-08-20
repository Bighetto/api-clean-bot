package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.usecase.ValidateUserBankUseCase;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ValidateUserBankService implements ValidateUserBankUseCase {

    private final ValidateUserBankV8UseCase validateUserBankV8UseCase;
    private final GetUserBankHubTokenService getUserBankHubTokenService;

    @Override
    public void execute(UploadBankUserRequestRestModel restModel) {
        String banco = restModel.getBankName();
        switch (banco) {
            case "Banco V8":
                this.validateUserBankV8UseCase.execute(restModel);
                break;
            case "Hub Credito":
                this.getUserBankHubTokenService.execute(restModel.getLogin(), restModel.getPassword());
                break;
            default:
                throw new RuntimeException("Banco invalido");
        }
    }
}
