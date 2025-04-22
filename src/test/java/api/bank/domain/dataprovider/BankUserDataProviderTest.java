package api.bank.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import api.bank.app.converter.BankUserModelToEntityConverter;
import api.bank.app.exception.BankUserNotFoundException;
import api.bank.app.model.BankUser;
import api.bank.app.provider.BankUserProvider;
import api.bank.app.repository.BankRepository;
import api.bank.app.repository.BankUserRepository;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.converter.UserModelToEntityConverter;
import api.security.auth.app.model.UserLogin;
import api.security.auth.app.provider.AuthProvider;
import api.security.auth.app.repository.UserRepository;
import api.security.auth.domain.dataprovider.AuthDataProvider;

@ExtendWith(MockitoExtension.class)
public class BankUserDataProviderTest {

    @Mock
    private BankUserRepository bankUserRepository;
    @Mock
    private BankUserModelToEntityConverter bankUserModelToEntityConverter;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BankUserDataProvider bankUserDataProvider;
    @Mock
    private UserModelToEntityConverter userModelToEntityConverter;

    private AuthDataProvider authDataProvider;
    private BankDataProvider bankDataProvider;

    

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authDataProvider = new AuthProvider(userRepository, userModelToEntityConverter);
        bankUserDataProvider = new BankUserProvider(bankUserRepository, authDataProvider, bankDataProvider, bankUserModelToEntityConverter);
    }

    @Test
    void shouldUploadABankUserWithSucessfull() {
        BankUser bankUser = new BankUser();

        this.bankUserDataProvider.uploadBankUser(bankUser);

        verify(bankUserRepository, times(1)).save(any(BankUser.class));
    }

    @Test
    void shouldReturnListOfBankUserEntitiesWhenUsersExist() {

        UserLogin userLogin = new UserLogin();

        BankUser bankUser = new BankUser();
        bankUser.setLogin("teste");
        bankUser.setNickname("teste");
        bankUser.setPassword("teste");

        BankUserEntity entity = new BankUserEntity();
        entity.setBankName("v8");
        entity.setId(UUID.randomUUID().toString());
        entity.setLogin("teste");

        when(bankUserRepository.findByUser(any())).thenReturn(List.of(bankUser));

        when(bankUserModelToEntityConverter.convertToEntity(any())).thenReturn(entity);

        List<BankUserEntity> result = this.bankUserDataProvider.findUsersBankByUser(userLogin);

        assertEquals(1, result.size());
        assertEquals("v8", result.get(0).getBankName());
       
    }

    @Test
    void shouldThrowRuntimeExceptionWhenNoUsersAreFound() {

        UserLogin userLogin = new UserLogin();

        when(bankUserRepository.findByUser(any())).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> bankUserDataProvider.findUsersBankByUser(userLogin));

        verify(bankUserRepository, times(1)).findByUser(userLogin);
        verify(bankUserModelToEntityConverter, never()).convertToEntity(any());
    }

    @Test
    void shouldReturnBankUserEntity() {
        BankUser bankUser = new BankUser();
        bankUser.setId("bankName");

        BankUserEntity bankUserEntity = new BankUserEntity();
        bankUserEntity.setBankName("bankName");

        when(this.bankUserRepository.findById("bankName")).thenReturn(Optional.of(bankUser));
        when(this.bankUserModelToEntityConverter.convertToEntity(any())).thenReturn(bankUserEntity);

        BankUserEntity response = this.bankUserDataProvider.findBankUserById("bankName");

        assertEquals(bankUserEntity.getBankName(), response.getBankName());
        verify(bankUserRepository, times(1)).findById(any());
    }

    @Test
    void shouldThrowsBankUserNotFoundException() {
        assertThrows(BankUserNotFoundException.class, () ->
            this.bankUserDataProvider.findBankUserById("null")
        );
    }
}
