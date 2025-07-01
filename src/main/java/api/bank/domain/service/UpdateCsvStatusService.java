package api.bank.domain.service;

import org.springframework.stereotype.Service;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.repository.ExecutorRepository;
import api.bank.domain.usecase.UpdateCsvStatusUseCase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UpdateCsvStatusService implements UpdateCsvStatusUseCase {

    private final ExecutorRepository executorRepository;

    @Override
    public void execute(String csvId, ProcessStatus status) {

        this.executorRepository.updateProcessStatusByCsvId(csvId, status.name());

    }

}
