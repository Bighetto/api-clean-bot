package api.bank.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import api.bank.app.converter.BankUserModelToEntityConverter;
import api.bank.app.model.BankUser;
import api.bank.app.provider.BankUserProvider;
import api.bank.app.repository.BankUserRepository;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;

@ExtendWith(MockitoExtension.class)
public class BankUserDataProviderTest {

    @Mock
    private BankUserRepository bankUserRepository;

    @Mock
    private BankUserModelToEntityConverter bankUserModelToEntityConverter;

    private BankUserDataProvider bankUserProvider;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        bankUserProvider = new BankUserProvider(bankUserRepository, null, null, bankUserModelToEntityConverter);
    
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

        List<BankUserEntity> result = this.bankUserProvider.findUsersBankByUser(userLogin);

        assertEquals(1, result.size());
        assertEquals("v8", result.get(0).getBankName());
       
    }

    @Test
    void shouldThrowRuntimeExceptionWhenNoUsersAreFound() {

        UserLogin userLogin = new UserLogin();

        when(bankUserRepository.findByUser(any())).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> bankUserProvider.findUsersBankByUser(userLogin));

        verify(bankUserRepository, times(1)).findByUser(userLogin);
        verify(bankUserModelToEntityConverter, never()).convertToEntity(any());
    }
}
