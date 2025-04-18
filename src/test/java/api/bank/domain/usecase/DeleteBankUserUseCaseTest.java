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
import api.bank.app.exception.InvalidBankUserIdException;
import api.bank.app.restmodel.DeleteBankUserRequestDTO;
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
        DeleteBankUserRequestDTO dto = new DeleteBankUserRequestDTO();
        dto.setBankUserId("testId");

        when(this.bankUserDataProvider.deleteBankUserById(dto.getBankUserId())).thenReturn(1);

        this.deleteBankUserUseCase.execute(dto);

        verify(this.bankUserDataProvider, times(1)).deleteBankUserById(anyString());
    }

    @Test 
    void shouldThrowInvalidBankUserIdExceptionWhenIdIsNullOrBlank() {
        DeleteBankUserRequestDTO dto = new DeleteBankUserRequestDTO();
        dto.setBankUserId(" ");

        assertThrows(InvalidBankUserIdException.class, () -> 
            this.deleteBankUserUseCase.execute(dto)
        );
    }

    @Test
    void shouldThrowBankUserNotFoundExceptionWhenUserDoesNotExist() {
        DeleteBankUserRequestDTO dto = new DeleteBankUserRequestDTO();
        dto.setBankUserId("nonExistingBankUser");

        when(this.bankUserDataProvider.deleteBankUserById(dto.getBankUserId())).thenReturn(0);

        assertThrows(BankUserNotFoundException.class, () -> 
            this.deleteBankUserUseCase.execute(dto)
        );
    }
}
