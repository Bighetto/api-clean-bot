package api.bank.domain.usecase;

import org.springframework.web.client.RestTemplate;

public interface ProcessRowHubBankUseCase {
    String execute(RestTemplate session, String acessToken, String customerCPF, String nomeTabelaFiltro);
}
