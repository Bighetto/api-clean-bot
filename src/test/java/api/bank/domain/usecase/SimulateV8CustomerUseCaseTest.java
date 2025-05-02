package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import api.bank.app.exception.SimulateV8CustomerException;
import api.bank.app.restmodel.BalancePeriods;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.service.SimulateV8CustomerService;

public class SimulateV8CustomerUseCaseTest {

    @Mock
    private V8BankConfigDataProvider v8BankConfigDataProvider;

    @Mock
    private RestTemplate restTemplate;

    private SimulateV8CustomerUseCase simulateV8CustomerUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        simulateV8CustomerUseCase = new SimulateV8CustomerService(v8BankConfigDataProvider);
    }

    @Test
    void shouldReturnAvailableAmountSuccessfully() throws Exception {
        String mockToken = "token123";
        String cpf = "12345678900";
        String balanceId = "balance-abc";
        double expectedAmount = 2500.00;

        List<BalancePeriods> balanceList = List.of(
            new BalancePeriods(1000.0, "2025-05-01"),
            new BalancePeriods(1500.0, "2025-06-01")
        );

        String apiUrl = "https://mockapi.v8bank.com/fgts/simulations";

        when(v8BankConfigDataProvider.getAudience()).thenReturn("https://mockapi.v8bank.com");

        String mockJsonResponse = """
            {
                "availableBalance": "2500.00"
            }
        """;

        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockJsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(apiUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        Double result = simulateV8CustomerUseCase.execute(restTemplate, mockToken, balanceList, cpf, balanceId);

        assertEquals(expectedAmount, result);
    }

    @Test
    void shouldThrowExceptionOnConnectionError() {
        when(v8BankConfigDataProvider.getAudience()).thenReturn("https://mockapi.v8bank.com");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Connection failed"));

        try {
            simulateV8CustomerUseCase.execute(restTemplate, "token", List.of(), "123", "balanceId");
        } catch (SimulateV8CustomerException e) {
            assertEquals("Error trying to consult balance", e.getMessage());
        }
    }
}
