package api.bank.domain.usecase;

import api.bank.domain.dto.ProcessamentoCsvDTO;

public interface StartCsvProcessingUseCase {
    String execute(ProcessamentoCsvDTO dto);
}
