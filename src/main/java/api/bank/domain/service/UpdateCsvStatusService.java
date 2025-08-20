package api.bank.domain.service;

import org.springframework.stereotype.Service;

import api.bank.app.enums.ProcessStatus;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.usecase.UpdateCsvStatusUseCase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UpdateCsvStatusService implements UpdateCsvStatusUseCase {

    private final ExecutorDataProvider executorDataProvider;

    @Override
    public void execute(String csvId, ProcessStatus status) {
        this.executorDataProvider.updateProcessStatusByCsvId(csvId, status.name());
    }
}
