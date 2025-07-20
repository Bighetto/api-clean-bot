package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.exception.StopExecutionException;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.entity.ExecutorEntity;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.StopExecutionUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StopExecutionService implements StopExecutionUseCase {
    
    private final ExecutorDataProvider executorDataProvider;
    private final CsvProcessManagerUseCase csvProcessManagerUseCase;

    @Override
    public Boolean execute(String email, String bankId) {

        ExecutorEntity executorFind = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(email, bankId);

        try {
            boolean isStoped = csvProcessManagerUseCase.pararProcessamento(executorFind.getProcessId());
            return isStoped;  
        } catch (Exception e) {
            throw new StopExecutionException("Erro ao tentar parar processamento");
        }
    }

}
