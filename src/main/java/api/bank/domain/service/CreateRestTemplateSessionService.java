package api.bank.domain.service;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;

@Component
public class CreateRestTemplateSessionService implements CreateRestTemplateSessionUseCase {

    private final RestTemplate restTemplate;

    public CreateRestTemplateSessionService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public RestTemplate execute() {
        return this.restTemplate;
    }
}
