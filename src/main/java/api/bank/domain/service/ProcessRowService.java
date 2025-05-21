package api.bank.domain.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.BalancePeriods;
import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
import api.bank.domain.usecase.ConsultV8CustomerBalanceUseCase;
import api.bank.domain.usecase.ProcessRowUseCase;
import api.bank.domain.usecase.SimulateV8CustomerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProcessRowService implements ProcessRowUseCase {

    private final ConsultV8CustomerBalanceUseCase consultV8CustomerBalanceUseCase;
    private final SimulateV8CustomerUseCase simulateV8CustomerUseCase;

    private static final List<String> ERROS_QUE_PERMITEM_RETRY = List.of(
        "Falha ao buscar o saldo disponivel!",
        "Serviço indisponivel no momento, tente novamente mais tarde",
        "Excedido o limite de requisições (máximo de 1 por segundo).",
        "Limite de requisições excedido, tente novamente mais tarde"
    );

    private static final int MAX_TENTATIVAS = 10;
    private static final long TEMPO_ESPERA_MS = 2000;

    @Override
    public String execute(RestTemplate session, String acessToken, String customerCPF) {
        int tentativa = 1;

        while (tentativa <= MAX_TENTATIVAS) {
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                return "PROCESSAMENTO INTERROMPIDO";
            }
            try {
                String resultado = processar(session, acessToken, customerCPF);
                System.out.println("Tentativa " + tentativa + ": " + resultado);

                if (isErro(resultado)) {
                    if (tentativa == MAX_TENTATIVAS) {
                        return resultado;
                    }
                    Thread.sleep(TEMPO_ESPERA_MS);
                    if (Thread.currentThread().isInterrupted()) {
                        Thread.currentThread().interrupt();
                        return "PROCESSAMENTO INTERROMPIDO";
                    }
                    tentativa++;
                    continue;
                }

                return resultado;

            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().interrupt();
                    return "PROCESSAMENTO INTERROMPIDO";
                }
                System.out.println("Tentativa " + tentativa + " falhou com exceção: " + e.getMessage());

                if (tentativa == MAX_TENTATIVAS) {
                    return "FALHA APÓS " + MAX_TENTATIVAS + " TENTATIVAS -> Último erro: " + e.getMessage();
                }

                try {
                    Thread.sleep(TEMPO_ESPERA_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "ERRO INTERNO: Thread interrompida";
                }
                tentativa++;
            }
        }

        return "FALHA INDEFINIDA";
    }
    private boolean isErro(String resultado) {
        if (resultado == null) return true;

        try {
            Double.parseDouble(resultado);
            return false;
        } catch (NumberFormatException e) {
        }

        return resultado.contains("Falha") || 
            resultado.contains("indisponivel") || 
            resultado.contains("Excedido") || 
            resultado.contains("Limite") || 
            resultado.contains("ERRO") || 
            resultado.equals("SEM SALDO") == false && resultado.matches(".*(NÃO AUTORIZADO|CPF INVÁLIDO).*") == false;
    }

    private String processar(RestTemplate session, String acessToken, String customerCPF) {
        try {
            ConsultV8CustomerBalanceResponse response = consultV8CustomerBalanceUseCase.execute(session, acessToken, customerCPF);

            Object balanceData = response.getBalancePeriods();

            if ("NÃO AUTORIZADO".equals(balanceData) || "CPF INVÁLIDO".equals(balanceData)) {
                return balanceData.toString();
            }

            if ("SEM SALDO".equals(balanceData)) {
                return "SEM SALDO";
            }

            if (response.getBalanceId() != null && balanceData instanceof List) {
                @SuppressWarnings("unchecked")
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

            return "SEM SALDO";

        } catch (Exception e) {
            return tratarMensagemDeErro(e.getMessage());
        }
    }

    private String tratarMensagemDeErro(String mensagem) {
        if (mensagem == null) {
            return "ERRO NA REQUISIÇÃO";
        }

        for (String erro : ERROS_QUE_PERMITEM_RETRY) {
            if (mensagem.contains(erro)) {
                return erro;
            }
        }

        return "ERRO NA REQUISIÇÃO";
    }
}
