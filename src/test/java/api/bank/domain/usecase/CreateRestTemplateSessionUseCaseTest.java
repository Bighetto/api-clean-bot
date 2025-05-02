package api.bank.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import api.bank.domain.service.CreateRestTemplateSessionService;

public class CreateRestTemplateSessionUseCaseTest {

    private CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;

    @BeforeEach
    void setup() {
        createRestTemplateSessionUseCase = new CreateRestTemplateSessionService();
    }

    @Test
    public void testExecuteReturnsRestTemplate() {
        RestTemplate restTemplate = createRestTemplateSessionUseCase.execute();
        
        assertNotNull(restTemplate);
        assertTrue(restTemplate instanceof RestTemplate);
    }
}
