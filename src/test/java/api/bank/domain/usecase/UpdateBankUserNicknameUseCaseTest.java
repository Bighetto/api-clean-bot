package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.converter.BankUserEntityToModelConverter;
import api.bank.app.model.BankUser;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.service.UpdateBankUserNicknameService;

public class UpdateBankUserNicknameUseCaseTest {

    @Mock
    private BankUserDataProvider bankUserDataProvider;

    @Mock
    private BankUserEntityToModelConverter bankUserEntityToModelConverter;

    private UpdateBankUserNicknameUseCase updateBankUserNicknameUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        updateBankUserNicknameUseCase = new UpdateBankUserNicknameService(bankUserDataProvider, bankUserEntityToModelConverter);
    }

    @Test
    void shouldUpdateNicknameWhenDifferent() {
        String bankUserId = "bankUserId";
        String newNickname = "newNickname";

        BankUserEntity bankUserEntity = new BankUserEntity();
        bankUserEntity.setId(bankUserId);
        bankUserEntity.setNickname("oldNickname");

        BankUser bankUser = new BankUser();
        bankUser.setId(bankUserId);

        when(this.bankUserDataProvider.findBankUserById(bankUserId)).thenReturn(bankUserEntity);

        this.updateBankUserNicknameUseCase.execute(bankUserId, newNickname);

        assertEquals(bankUserEntity.getNickname(), newNickname);
        verify(this.bankUserEntityToModelConverter, times(1)).convertToModel(bankUserEntity);
        verify(this.bankUserDataProvider, times(1)).uploadBankUser(any());
    }

    @Test
    void shouldNotUpdateNicknameWhenSame() {
        String bankUserId = "bankUserId";
        String newNickname = "oldNickname";

        BankUserEntity bankUserEntity = new BankUserEntity();
        bankUserEntity.setId(bankUserId);
        bankUserEntity.setNickname("oldNickname");
        
        when(this.bankUserDataProvider.findBankUserById(bankUserId)).thenReturn(bankUserEntity);

        this.updateBankUserNicknameUseCase.execute(bankUserId, newNickname);

        verify(bankUserDataProvider, never()).uploadBankUser(any());
        verify(bankUserEntityToModelConverter, never()).convertToModel(any());
    }
}
