package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.service.FindUsersBankByUserDocumentService;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.entity.UserEntity;

public class FindUsersBankByUserDocumentUseCaseTest {

    @Mock
    private AuthDataProvider authDataProvider;

    FindUsersBankByUserDocumentUseCase useCase;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        useCase = new FindUsersBankByUserDocumentService(authDataProvider);

    }

    @Test
    void shouldReturnListOfUserBanksWithSuccessful(){

        UserEntity userEntity = new UserEntity();

        BankUserEntity bankUserentity = new BankUserEntity();
        bankUserentity.setBankName("v8");
        bankUserentity.setId(UUID.randomUUID().toString());

        userEntity.setUserBanks(List.of(bankUserentity));

        when(this.authDataProvider.findByEmail(anyString())).thenReturn(userEntity);

        List<BankUserEntity> result = this.useCase.execute("test");

        assertTrue(!result.isEmpty());

    }
}
