package api.bank.domain.usecase;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.BalancePeriods;

public interface SimulateV8CustomerUseCase {
    Double execute(RestTemplate session, String acessToken, List<BalancePeriods> listBalance, String customerCPF, String customerBalanceId);
}
