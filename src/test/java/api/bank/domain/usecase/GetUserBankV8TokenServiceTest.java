package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import api.bank.app.exception.GetUserBankV8TokenException;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.service.GetUserBankV8TokenService;

public class GetUserBankV8TokenServiceTest {

    @Mock
    private V8BankConfigDataProvider v8BankConfigDataProvider;

    @Mock
    private CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;

    @Mock
    private RestTemplate restTemplate;

    private GetUserBankV8TokenUseCase getUserBankV8TokenUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        getUserBankV8TokenUseCase = new GetUserBankV8TokenService(v8BankConfigDataProvider, createRestTemplateSessionUseCase);
    }

    @Test
    public void shouldReturnAcessToken() {
        when(v8BankConfigDataProvider.getAudience()).thenReturn("audience");
        when(v8BankConfigDataProvider.getClientId()).thenReturn("clientId");
        when(v8BankConfigDataProvider.getGrantType()).thenReturn("grantType");
        when(v8BankConfigDataProvider.getScope()).thenReturn("scope");
        when(v8BankConfigDataProvider.getV8BankURL()).thenReturn("http://v8bank.com");

        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn("{\"access_token\":\"fake_access_token\"}");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(responseEntity);

        when(createRestTemplateSessionUseCase.execute()).thenReturn(restTemplate);

        String token = getUserBankV8TokenUseCase.execute("user", "password");

        assertNotNull(token);
        assertEquals("fake_access_token", token);
    }

    @Test
    public void shoudlThrowsGetUserBankV8TokenException() {
        when(v8BankConfigDataProvider.getAudience()).thenReturn("audience");
        when(v8BankConfigDataProvider.getClientId()).thenReturn("clientId");
        when(v8BankConfigDataProvider.getGrantType()).thenReturn("grantType");
        when(v8BankConfigDataProvider.getScope()).thenReturn("scope");
        when(v8BankConfigDataProvider.getV8BankURL()).thenReturn("http://v8bank.com");

        when(createRestTemplateSessionUseCase.execute()).thenReturn(restTemplate);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(GetUserBankV8TokenException.class, () -> {
            getUserBankV8TokenUseCase.execute("user", "password");
        });
    }
}
