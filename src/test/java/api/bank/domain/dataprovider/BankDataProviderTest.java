package api.bank.domain.dataprovider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.converter.BankModelToEntityConverter;
import api.bank.app.model.Bank;
import api.bank.app.provider.BankProvider;
import api.bank.app.repository.BankRepository;
import api.bank.domain.entity.BankEntity;

public class BankDataProviderTest {

    @Mock
    BankRepository bankRepository;

    @Mock
    BankModelToEntityConverter bankModelToEntityConverter;

    BankDataProvider bankDataProvider;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        bankDataProvider = new BankProvider(bankRepository, bankModelToEntityConverter);
    }
    
    @Test
    void shouldReturnBank() {
        Bank bank = new Bank();
        bank.setName("testName");

        BankEntity bankEntity = new BankEntity();
        bankEntity.setName("testName");

        when(bankRepository.findByName("testName")).thenReturn(bank);
        when(bankModelToEntityConverter.convertToEntity(bank)).thenReturn(bankEntity);

        BankEntity response = this.bankDataProvider.findByName("testName");

        assert(response.getName() != null);
        verify(bankRepository, times(1)).findByName("testName");
    }
}
