package api.bank.domain.usecase;

import api.bank.domain.dto.CsvExecutionHistoryDto;

public interface SaveProcessingLogUseCase {
    void execute(CsvExecutionHistoryDto dto);
}
