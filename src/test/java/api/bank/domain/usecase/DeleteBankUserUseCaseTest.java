package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.exception.BankUserNotFoundException;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.service.DeleteBankUserService;

public class DeleteBankUserUseCaseTest {

    @Mock
    private BankUserDataProvider bankUserDataProvider;

    DeleteBankUserUseCase deleteBankUserUseCase;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        deleteBankUserUseCase = new DeleteBankUserService(bankUserDataProvider);
    }

    @Test
    void shouldDeleteBankSucessfuly() {
        String bankUserId = "testId";

        when(this.bankUserDataProvider.deleteBankUserById(bankUserId)).thenReturn(1);

        this.deleteBankUserUseCase.execute(bankUserId);

        verify(this.bankUserDataProvider, times(1)).deleteBankUserById(anyString());
    }

    @Test
    void shouldThrowBankUserNotFoundExceptionWhenUserDoesNotExist() {
        String bankUserId = "testId";

        when(this.bankUserDataProvider.deleteBankUserById(bankUserId)).thenReturn(0);

        assertThrows(BankUserNotFoundException.class, () -> 
            this.deleteBankUserUseCase.execute(bankUserId)
        );
    }
}
