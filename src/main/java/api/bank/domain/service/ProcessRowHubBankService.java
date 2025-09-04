package api.bank.domain.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import api.bank.domain.usecase.ProcessRowHubBankUseCase;
import api.bank.domain.usecase.SimulateHubBankCustomerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProcessRowHubBankService implements ProcessRowHubBankUseCase {

    private final SimulateHubBankCustomerUseCase simulateHubBankCustomerUseCase;

    private static final List<String> ERROS_QUE_PERMITEM_RETRY = List.of(
            "Excedido o limite de requisições",
            "Erro enviando requisição para o banco Hub",
            "Erro de conexão com o banco Hub");

    private static final int MAX_TENTATIVAS = 10;
    private static final long TEMPO_ESPERA_MS = 2000;

    @Override
    public String execute(RestTemplate session, String acessToken, String customerCPF, String nomeTabelaFiltro,
            String bancarizador) {
        int tentativa = 1;

        while (tentativa <= MAX_TENTATIVAS) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("[HUB] Thread interrompida antes da tentativa " + tentativa + ", saindo...");
                return "PROCESSAMENTO INTERROMPIDO";
            }

            try {
                String resultado = processar(session, acessToken, customerCPF, nomeTabelaFiltro, bancarizador);
                System.out.println("[HUB] Tentativa " + tentativa + ": " + resultado);

                if (isErro(resultado)) {
                    if (tentativa == MAX_TENTATIVAS) {
                        return resultado;
                    }
                    try {
                        Thread.sleep(TEMPO_ESPERA_MS);
                    } catch (InterruptedException ie) {
                        System.out.println("[HUB] Thread interrompida durante sleep, saindo...");
                        Thread.currentThread().interrupt();
                        return "PROCESSAMENTO INTERROMPIDO";
                    }
                    tentativa++;
                    continue;
                }

                return resultado;

            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("[HUB] Thread interrompida após exceção, saindo...");
                    return "PROCESSAMENTO INTERROMPIDO";
                }
                System.out.println("[HUB] Tentativa " + tentativa + " falhou com exceção: " + e.getMessage());

                if (tentativa == MAX_TENTATIVAS) {
                    return "FALHA APÓS " + MAX_TENTATIVAS + " TENTATIVAS -> Último erro: " + e.getMessage();
                }

                try {
                    Thread.sleep(TEMPO_ESPERA_MS);
                } catch (InterruptedException ie) {
                    System.out.println("[HUB] Thread interrompida durante sleep na exceção, saindo...");
                    Thread.currentThread().interrupt();
                    return "PROCESSAMENTO INTERROMPIDO";
                }
                tentativa++;
            }
        }

        return "FALHA INDEFINIDA";
    }

    private boolean isErro(String resultado) {
        if (resultado == null)
            return true;

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
                resultado.equals("SEM SALDO") == false
                        && resultado.matches(".*(NÃO AUTORIZADO|CPF INVÁLIDO).*") == false;
    }

    private String processar(RestTemplate session, String acessToken, String customerCPF, String nomeTabelaFiltro,
            String bancarizador) {
        try {
            return simulateHubBankCustomerUseCase.execute(session, acessToken, customerCPF, nomeTabelaFiltro,
                    bancarizador);
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
