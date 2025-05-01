package api.bank.domain.usecase;

import org.springframework.web.client.RestTemplate;

public interface ProcessRowUseCase {
    String execute(RestTemplate session, String acessToken, String customerCPF);
}
