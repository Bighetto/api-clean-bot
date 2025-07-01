package api.bank.domain.service;

import org.springframework.stereotype.Service;

import api.bank.app.repository.ExecutorRepository;
import api.bank.domain.usecase.UpdateCsvIdProcessUseCase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UpdateCsvIdProcessService implements UpdateCsvIdProcessUseCase {

    private final ExecutorRepository executorRepository;

    @Override
    public void execute(String csvId, String processId) {

        this.executorRepository.updateProcessIdByCsvId(csvId, processId);

    }

}
