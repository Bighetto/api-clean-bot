package api.bank.domain.dataprovider;

import api.bank.app.model.CsvExecutionHistory;

public interface CsvExecutionHistoryDataProvider {
    void save(CsvExecutionHistory log);
}
