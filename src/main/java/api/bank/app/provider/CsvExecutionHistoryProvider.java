package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.app.model.CsvExecutionHistory;
import api.bank.app.repository.CsvExecutionHistoryRepository;
import api.bank.domain.dataprovider.CsvExecutionHistoryDataProvider;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CsvExecutionHistoryProvider implements CsvExecutionHistoryDataProvider {
    
    private final CsvExecutionHistoryRepository csvExecutionHistoryRepository;

    @Override
    public void save(CsvExecutionHistory log) {
        this.csvExecutionHistoryRepository.save(log);
    }

}
