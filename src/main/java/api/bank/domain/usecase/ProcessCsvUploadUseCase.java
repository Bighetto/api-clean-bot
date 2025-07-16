package api.bank.domain.usecase;

import org.springframework.web.multipart.MultipartFile;

public interface ProcessCsvUploadUseCase {
    String execute(MultipartFile file, String email, String bankId);
}
