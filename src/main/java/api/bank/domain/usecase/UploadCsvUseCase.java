package api.bank.domain.usecase;

import api.bank.app.restmodel.UploadCsvDTO;

public interface UploadCsvUseCase {
    String execute(UploadCsvDTO dto);
}
