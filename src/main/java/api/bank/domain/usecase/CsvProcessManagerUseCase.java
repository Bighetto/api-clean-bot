package api.bank.domain.usecase;

import java.util.List;

public interface CsvProcessManagerUseCase {

    public String iniciarProcessamento(String csvId, List<String> usuarios, String email, LogSenderUseCase logSender,
            String bankName, String nomeTabelaFiltro);

    public boolean pararProcessamento(String processoId);
}
