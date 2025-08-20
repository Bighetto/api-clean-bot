package api.bank.domain.service;

import org.springframework.stereotype.Service;

import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.usecase.UpdateCsvIdProcessUseCase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UpdateCsvIdProcessService implements UpdateCsvIdProcessUseCase {

    private final ExecutorDataProvider executorDataProvider;

    @Override
    public void execute(String csvId, String processId) {
        this.executorDataProvider.updateProcessIdByCsvId(csvId, processId);
    }
}
