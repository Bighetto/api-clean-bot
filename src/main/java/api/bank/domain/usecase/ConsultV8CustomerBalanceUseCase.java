package api.bank.domain.usecase;

import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;

public interface ConsultV8CustomerBalanceUseCase {
    ConsultV8CustomerBalanceResponse execute(RestTemplate session, String acessToken, String customerCPF);
}
