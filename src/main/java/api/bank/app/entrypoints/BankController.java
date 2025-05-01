package api.bank.app.entrypoints;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import api.bank.app.converter.BankUserEntityToRestModelConverter;
import api.bank.app.exception.BankUserAlreadyExistsException;
import api.bank.app.exception.BankUserNotFoundException;
import api.bank.app.exception.UserBankV8ValidationException;
import api.bank.app.restmodel.BankUserRestModel;
import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
import api.bank.app.restmodel.SimulateTest;
import api.bank.app.restmodel.TestRequestDTO;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.ConsultV8CustomerBalanceUseCase;
import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;
import api.bank.domain.usecase.DeleteBankUserUseCase;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;
import api.bank.domain.usecase.SimulateV8CustomerUseCase;
import api.bank.domain.usecase.UploadBankUserUseCase;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import api.security.auth.app.security.SecurityConfig;
import lombok.AllArgsConstructor;

@RequestMapping(value = "/bank")
@AllArgsConstructor
@CrossOrigin
@RestController
public class BankController implements BankResource {
    
    private final FindUsersBankByUserDocumentUseCase findUsersBankByUserDocumentUseCase;
    private final BankUserEntityToRestModelConverter bankUserEntityToRestModelConverter;
    private final UploadBankUserUseCase uploadBankUserUseCase;
    private final SecurityConfig securityConfig;
    private final ValidateUserBankV8UseCase validateUserBankV8UseCase;
    private final DeleteBankUserUseCase deleteBankUserUseCase;
    private final ConsultV8CustomerBalanceUseCase consultV8CustomerBalanceUseCase;
    private final CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;
    private final SimulateV8CustomerUseCase simulateV8CustomerUseCase;

    @Override
    @PostMapping
    public ResponseEntity<String> uploadBankUser(@RequestBody UploadBankUserRequestRestModel restModel) {
        try {
            this.validateUserBankV8UseCase.execute(restModel);

            String encryptPassword = this.securityConfig.passwordEncoder().encode(restModel.getPassword());
            restModel.setPassword(encryptPassword);

            this.uploadBankUserUseCase.execute(restModel);

            return ResponseEntity.ok().build();

        } catch (UserBankV8ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (BankUserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Override
    @GetMapping("/{email}")
    public ResponseEntity<List<BankUserRestModel>> findUsersBankByUser(@PathVariable String email) {

        try{
            List<BankUserEntity> entitysList = this.findUsersBankByUserDocumentUseCase.execute(email);

            if (entitysList.isEmpty()) {
                throw new RuntimeException("That user has no bank accounts");
            }
    
            List<BankUserRestModel> restModels = entitysList.stream()
                .map(entity -> bankUserEntityToRestModelConverter.convertToModel(entity))
                .collect(Collectors.toList());
    
            return ResponseEntity.ok().body(restModels);
        
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    @DeleteMapping("/{bankUserId}")
    public ResponseEntity<String> deleteBankUser(@PathVariable String bankUserId) {
        try {
            this.deleteBankUserUseCase.execute(bankUserId);

            return ResponseEntity.ok().build();
        } catch (BankUserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank Account not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/teste")
    public ResponseEntity<ConsultV8CustomerBalanceResponse> test(@RequestBody TestRequestDTO dto) {
        
        RestTemplate session = this.createRestTemplateSessionUseCase.execute();

        ConsultV8CustomerBalanceResponse response = this.consultV8CustomerBalanceUseCase.execute(session, dto.token(), dto.cpf());
        
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/teste2")
    public ResponseEntity<Object> test2(@RequestBody SimulateTest dto) {
        
        RestTemplate session = this.createRestTemplateSessionUseCase.execute();

        Object response = this.simulateV8CustomerUseCase.execute(session, dto.token(), dto.response(), dto.cpf(), dto.balanceId());
        
        return ResponseEntity.ok().body(response);
    }
}
