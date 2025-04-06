package api.bank.domain.usecase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.service.UploadBankUserService;

public class UploadBankUserUseCaseTest {
    
    @Mock
    private BankUserDataProvider bankUserDataProvider;

    private UploadBankUserUseCase uploadBankUserUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        uploadBankUserUseCase = new UploadBankUserService(bankUserDataProvider);
    }

    @Test
    void shouldCallBankUserDataProvider() {
        UploadBankUserRequestRestModel uploadBankUserRequestRestModel = new UploadBankUserRequestRestModel();

        this.uploadBankUserUseCase.execute(uploadBankUserRequestRestModel);

        verify(bankUserDataProvider, times(1)).uploadBankUser(uploadBankUserRequestRestModel);
    }
}
