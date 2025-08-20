package api.bank.domain.usecase;

import org.springframework.web.client.RestTemplate;

public interface SimulateHubBankCustomerUseCase {
    String execute(RestTemplate session, String acessToken, String customerCpf);
}
