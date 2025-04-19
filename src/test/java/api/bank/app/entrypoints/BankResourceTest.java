package api.bank.app.entrypoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import api.bank.app.converter.BankUserEntityToRestModelConverter;
import api.bank.app.exception.BankUserAlreadyExistsException;
import api.bank.app.exception.BankUserNotFoundException;
import api.bank.app.exception.UserBankV8ValidationException;
import api.bank.app.restmodel.BankUserRestModel;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.DeleteBankUserUseCase;
import api.bank.domain.usecase.FindUsersBankByUserDocumentUseCase;
import api.bank.domain.usecase.UploadBankUserUseCase;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import api.security.auth.app.security.SecurityConfig;

@ExtendWith(MockitoExtension.class)
public class BankResourceTest {

    @Mock
    private FindUsersBankByUserDocumentUseCase findUsersBankByUserDocumentUseCase;
    @Mock
    private BankUserEntityToRestModelConverter bankUserEntityToRestModelConverter;
    @Mock
    private UploadBankUserUseCase uploadBankUserUseCase;
    @Mock
    private SecurityConfig securityConfig; 
    @Mock 
    PasswordEncoder passwordEncoder;
    @Mock
    private ValidateUserBankV8UseCase validateUserBankV8UseCase;
    @Mock
    private DeleteBankUserUseCase deleteBankUserUseCase;

    BankResource controller;

    @BeforeEach
    void setUp(){
        controller = new BankController(findUsersBankByUserDocumentUseCase, bankUserEntityToRestModelConverter, uploadBankUserUseCase, securityConfig, validateUserBankV8UseCase, deleteBankUserUseCase);
    }

    @Test
    void testUploadBankUserSuccess() throws Exception {
        UploadBankUserRequestRestModel restModel = new UploadBankUserRequestRestModel();
        restModel.setLogin("testuser");
        restModel.setPassword("testpassword");
        restModel.setBankName("testBankName");

        String encryptedPassword = "encryptedPassword";

        doNothing().when(validateUserBankV8UseCase).execute(any());
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        doNothing().when(uploadBankUserUseCase).execute(any());

        try {
            
            ResponseEntity<String> response = controller.uploadBankUser(restModel);

            assertEquals(HttpStatus.OK, response.getStatusCode());
    
            verify(validateUserBankV8UseCase, times(1)).execute(restModel);
            verify(uploadBankUserUseCase, times(1)).execute(restModel);
    
        } catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    void testUploadBankUserWithUserBankV8ValidationException() throws Exception {
        UploadBankUserRequestRestModel restModel = new UploadBankUserRequestRestModel();
        restModel.setLogin("invalidUser");
        restModel.setPassword("testpassword");

        doThrow(new UserBankV8ValidationException(null)).when(validateUserBankV8UseCase).execute(any());

        ResponseEntity<String> response = controller.uploadBankUser(restModel);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testUploadBankUserWithBankUserAlreadyExistsException() throws Exception {
        UploadBankUserRequestRestModel restModel = new UploadBankUserRequestRestModel();
        restModel.setLogin("invalidUser");
        restModel.setPassword("testpassword");
        String encryptedPassword = "encryptedPassword";

        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        doThrow(new BankUserAlreadyExistsException(null)).when(uploadBankUserUseCase).execute(any());

        ResponseEntity<String> response = controller.uploadBankUser(restModel);

        assertEquals(400, response.getStatusCodeValue());
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

    @Test
    void shouldDeleteBankUserSucessfuly() {
        String bankUserId = "testId";

        ResponseEntity<String> response = this.controller.deleteBankUser(bankUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenThrowsException() {
        String bankUserId = "testId";

        doThrow(BankUserNotFoundException.class).when(this.deleteBankUserUseCase).execute(bankUserId);

        ResponseEntity<String> response = this.controller.deleteBankUser(bankUserId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnInternalServerErrorWhenThrowsException() {
        String bankUserId = "testId";

        doThrow(RuntimeException.class).when(this.deleteBankUserUseCase).execute(bankUserId);

        ResponseEntity<String> response = this.controller.deleteBankUser(bankUserId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
}