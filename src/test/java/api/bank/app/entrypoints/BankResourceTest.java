package api.bank.app.entrypoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import api.bank.app.converter.BankUserEntityToRestModelConverter;
import api.bank.app.restmodel.BankUserRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;

@ExtendWith(MockitoExtension.class)
public class BankResourceTest {

    @Mock
    private FindUsersBankByUserDocumentUseCase findUsersBankByUserDocumentUseCase;
    @Mock
    private BankUserEntityToRestModelConverter bankUserEntityToRestModelConverter;

    BankResource controller;

    @BeforeEach
    void setUp(){
        controller = new BankController(findUsersBankByUserDocumentUseCase, bankUserEntityToRestModelConverter);
    }


    @Test
    void shouldReturnListOfUserBankWithSucessful(){

        String userId = UUID.randomUUID().toString(); // cria um ID de teste


        BankUserEntity userEntity = new BankUserEntity();
        userEntity.setBankName("v8");
        userEntity.setId(userId);
        userEntity.setLogin("teste");
        userEntity.setNickname("teste");

        BankUserRestModel restModel = new BankUserRestModel();
        restModel.setBankName("v8");
        restModel.setNickname("teste");
        restModel.setUsername("teste");
        restModel.setId(userId);


        when(this.findUsersBankByUserDocumentUseCase.execute(anyString())).thenReturn(List.of(userEntity));

        when(this.bankUserEntityToRestModelConverter.convertToModel(any())).thenReturn(restModel);

        ResponseEntity<List<BankUserRestModel>> response = this.controller.findUsersBankByUser("teste@teste.com");

        assertEquals(1, response.getBody().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenBankUserIsNotFoundWithSucessful(){


        when(this.findUsersBankByUserDocumentUseCase.execute(anyString())).thenReturn(List.of());


        ResponseEntity<List<BankUserRestModel>> response = this.controller.findUsersBankByUser("teste@teste.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}
