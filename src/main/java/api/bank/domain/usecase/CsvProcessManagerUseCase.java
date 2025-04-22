package api.bank.domain.usecase;

import java.util.List;


public interface CsvProcessManagerUseCase {
    
    public String iniciarProcessamento(String csvId, List<String> usuarios, LogSenderUseCase logSender);

    public boolean pararProcessamento(String processoId);
}
