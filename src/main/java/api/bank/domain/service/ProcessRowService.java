package api.bank.domain.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.BalancePeriods;
import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.ConsultV8CustomerBalanceUseCase;
import api.bank.domain.usecase.ProcessRowUseCase;
import api.bank.domain.usecase.SimulateV8CustomerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProcessRowService implements ProcessRowUseCase {
    
    private final V8BankConfigDataProvider v8BankConfigDataProvider;
    private final ConsultV8CustomerBalanceUseCase consultV8CustomerBalanceUseCase;
    private final SimulateV8CustomerUseCase simulateV8CustomerUseCase;

    @Override
    public String execute(RestTemplate session, String acessToken, String customerCPF) {
        try {
            ConsultV8CustomerBalanceResponse response = consultV8CustomerBalanceUseCase.execute(session, acessToken, customerCPF);

            Object balanceData = response.getBalancePeriods();

            if ("NÃO AUTORIZADO".equals(balanceData) || "CPF INVÁLIDO".equals(balanceData)) {
                return balanceData.toString();
            }

            if (!"SEM SALDO".equals(balanceData) && response.getBalanceId() != null) {
                List<BalancePeriods> balancePeriods = (List<BalancePeriods>) balanceData;

                Double simulationResult = simulateV8CustomerUseCase.execute(
                    session,
                    acessToken,
                    balancePeriods,
                    customerCPF,
                    response.getBalanceId()
                );

                if (simulationResult != null) {
                    return simulationResult.toString();
                }
            }

        } catch (Exception e) {
            return "ERRO NA REQUISIÇÃO";
        }

        return "SEM SALDO";
    }
    
}
