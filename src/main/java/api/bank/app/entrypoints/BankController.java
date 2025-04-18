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

import api.bank.app.converter.BankUserEntityToRestModelConverter;
import api.bank.app.exception.BankUserAlreadyExistsException;
import api.bank.app.exception.BankUserNotFoundException;
import api.bank.app.exception.InvalidBankUserIdException;
import api.bank.app.exception.UserBankV8ValidationException;
import api.bank.app.restmodel.BankUserRestModel;
import api.bank.app.restmodel.DeleteBankUserRequestDTO;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.DeleteBankUserUseCase;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;
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
    @DeleteMapping
    public ResponseEntity<String> deleteBankUser(@RequestBody DeleteBankUserRequestDTO dto) {
        try {
            this.deleteBankUserUseCase.execute(dto);

            return ResponseEntity.ok().build();
        } catch (BankUserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank Account not found");
        } catch (InvalidBankUserIdException e) {
            return ResponseEntity.badRequest().body("The bank user ID is invalid.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
