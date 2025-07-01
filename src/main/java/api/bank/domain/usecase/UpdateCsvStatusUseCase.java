package api.bank.domain.usecase;

import api.bank.app.enums.ProcessStatus;

public interface UpdateCsvStatusUseCase {

    void execute(String csvId, ProcessStatus status);

}
