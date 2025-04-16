package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.exception.BankUserAlreadyExistsException;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankEntity;
import api.bank.domain.service.UploadBankUserService;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.entity.UserEntity;

public class UploadBankUserUseCaseTest {
    
    @Mock
    private BankUserDataProvider bankUserDataProvider;
    @Mock
    private AuthDataProvider authDataProvider;
    @Mock
    private BankDataProvider bankDataProvider;

    private UploadBankUserUseCase uploadBankUserUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        uploadBankUserUseCase = new UploadBankUserService(bankUserDataProvider, authDataProvider, bankDataProvider);
    }

    @Test
    void shouldCallBankUserDataProvider() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("testEmail");
        userEntity.setDocument("testDocument");

        BankEntity bankEntity = new BankEntity();
        bankEntity.setName("testName");
        bankEntity.setId("bankId");

        UploadBankUserRequestRestModel uploadBankUserRequestRestModel = new UploadBankUserRequestRestModel();
        uploadBankUserRequestRestModel.setUserEmail("testEmail");
        uploadBankUserRequestRestModel.setBankName("testName");
        uploadBankUserRequestRestModel.setLogin("testLogin");
        uploadBankUserRequestRestModel.setPassword("testPassword");
        uploadBankUserRequestRestModel.setNickname("testNickname");

        when(authDataProvider.findByEmail("testEmail")).thenReturn(userEntity);
        when(bankDataProvider.findByName(anyString())).thenReturn(bankEntity);

        
        UserEntity mockUser = authDataProvider.findByEmail("testEmail");
        assert mockUser != null : "UserEntity não pode ser nulo";

        uploadBankUserUseCase.execute(uploadBankUserRequestRestModel);

        verify(bankUserDataProvider, times(1)).uploadBankUser(any());
    }

    @Test
    void shouldThrowsBankUserAlreadyExistsException() {
        UploadBankUserRequestRestModel uploadBankUserRequestRestModel = new UploadBankUserRequestRestModel();
        uploadBankUserRequestRestModel.setLogin("testLogin");
        uploadBankUserRequestRestModel.setUserEmail("testEmail");
        uploadBankUserRequestRestModel.setBankName("testName");

        BankEntity bankEntity = new BankEntity();
        bankEntity.setName("testName");
        bankEntity.setId("bankId");

        when(bankDataProvider.findByName(anyString())).thenReturn(bankEntity);

        when(this.bankUserDataProvider.existsByLoginAndBankId(uploadBankUserRequestRestModel.getLogin(), bankEntity.getId())).thenReturn(true);

        assertThrows(BankUserAlreadyExistsException.class, () -> 
            this.uploadBankUserUseCase.execute(uploadBankUserRequestRestModel)
        );
    }
}
