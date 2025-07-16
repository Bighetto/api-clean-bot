package api.bank.domain.usecase;

import api.bank.domain.dto.FindCsvStatusDTO;

public interface CsvStatusCheckerUseCase {
    FindCsvStatusDTO execute(String email, String bankId);
}
