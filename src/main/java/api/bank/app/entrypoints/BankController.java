package api.bank.app.entrypoints;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.bank.app.converter.BankUserEntityToRestModelConverter;
import api.bank.app.restmodel.BankUserRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;
import lombok.AllArgsConstructor;

@RequestMapping(value = "/bank")
@AllArgsConstructor
@CrossOrigin
@RestController
public class BankController implements BankResource {
    
    private final FindUsersBankByUserDocumentUseCase findUsersBankByUserDocumentUseCase;
    private final BankUserEntityToRestModelConverter bankUserEntityToRestModelConverter;

    @GetMapping
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok().body("Teste");
    }

    @Override
    @GetMapping("/{document}")
    public ResponseEntity<List<BankUserRestModel>> findUsersBankByUser(@PathVariable String document) {

        List<BankUserEntity> entitysList = this.findUsersBankByUserDocumentUseCase.execute(document);

        if (entitysList.isEmpty()) {
            throw new RuntimeException("That user has no bank accounts");
        }

        List<BankUserRestModel> restModels = entitysList.stream()
            .map(entity -> bankUserEntityToRestModelConverter.convertToModel(entity))
            .collect(Collectors.toList());

        return ResponseEntity.ok().body(restModels);
    }

}
