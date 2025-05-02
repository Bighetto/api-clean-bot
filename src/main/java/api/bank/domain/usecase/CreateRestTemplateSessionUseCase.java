package api.bank.domain.usecase;

import org.springframework.web.client.RestTemplate;

public interface CreateRestTemplateSessionUseCase {
    RestTemplate execute();
}
