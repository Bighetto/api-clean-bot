package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.exception.ConflictException;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.dto.ProcessamentoCsvDTO;
import api.bank.domain.entity.BankEntity;
import api.bank.domain.entity.ExecutorEntity;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import api.bank.domain.usecase.StartCsvProcessingUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StartCsvProcessingService implements StartCsvProcessingUseCase {

    private final ExecutorDataProvider executorDataProvider;
    private final CsvProcessManagerUseCase csvProcessManagerUseCase;
    private final LogSenderUseCase logSender;
    private final BankDataProvider bankDataProvider;

    @Override
    public String execute(ProcessamentoCsvDTO dto) {
        BankEntity bankEntity = this.bankDataProvider.findByName(dto.getBankName());

        ExecutorEntity executor = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(dto.getEmail(),
                bankEntity.getId());

        if (executor.getProcessStatus() == ProcessStatus.EM_ANDAMENTO) {
            throw new ConflictException("Não é possível iniciar, o processo já está em andamento.");
        }

        String processoId = csvProcessManagerUseCase.iniciarProcessamento(
                dto.getCsvId(),
                dto.getUsuarios(),
                dto.getEmail(),
                logSender,
                bankEntity.getName());

        return processoId;
    }
}
