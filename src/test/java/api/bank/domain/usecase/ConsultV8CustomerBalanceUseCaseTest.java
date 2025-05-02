package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import api.bank.app.exception.ConsultV8CustomerBalanceException;
import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.service.ConsultV8CustomerBalanceService;

public class ConsultV8CustomerBalanceUseCaseTest {

    @Mock
    private V8BankConfigDataProvider v8BankConfigDataProvider;

    @Mock
    private RestTemplate restTemplate;

    private ConsultV8CustomerBalanceUseCase consultV8CustomerBalanceUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        consultV8CustomerBalanceUseCase = new ConsultV8CustomerBalanceService(v8BankConfigDataProvider);
    }

    @Test
    void shouldReturnBalanceSuccessfully() {
        String accessToken = "mocked_token";
        String cpf = "12345678900";
        String url = "https://v8.com/api/fgts/balance";

        when(v8BankConfigDataProvider.getAudience()).thenReturn("https://v8.com/api");

        String fakeJson = """
            {
                "data": [
                    {
                        "id": "balance-123",
                        "periods": [
                            {
                                "amount": 100.0,
                                "dueDate": "2025-05-01"
                            }
                        ]
                    }
                ]
            }
        """;

        ResponseEntity<String> fakeResponse = new ResponseEntity<>(fakeJson, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenReturn(fakeResponse);

        ConsultV8CustomerBalanceResponse result = consultV8CustomerBalanceUseCase.execute(restTemplate, accessToken, cpf);

        assertNotNull(result);
        assertEquals("balance-123", result.getBalanceId());
        assertTrue(result.getValidCustomer());
    }

    @Test
    void shouldThrowExceptionWhenInvalidDocument() {
        String accessToken = "mocked_token";
        String cpf = "invalid_cpf";
        String url = "https://v8.com/api/fgts/balance";

        when(v8BankConfigDataProvider.getAudience()).thenReturn("https://v8.com/api");

        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ConsultV8CustomerBalanceException.class,
                () -> consultV8CustomerBalanceUseCase.execute(restTemplate, accessToken, cpf));
    }
}
