package api.bank.domain.dataprovider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.bank.app.model.Bank;
import api.bank.app.provider.BankProvider;
import api.bank.app.repository.BankRepository;

public class BankDataProviderTest {

    @Mock
    BankRepository bankRepository;

    BankDataProvider bankDataProvider;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        bankDataProvider = new BankProvider(bankRepository);
    }
    
    @Test
    void shouldReturnBank() {
        Bank bank = new Bank();
        bank.setName("testName");

        when(bankRepository.findByName("testName")).thenReturn(bank);

        Bank response = this.bankDataProvider.findByName("testName");

        assert(response.getName() != null);
        verify(bankRepository, times(1)).findByName("testName");
    }
}
