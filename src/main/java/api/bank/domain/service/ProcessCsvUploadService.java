package api.bank.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.restmodel.UploadCsvDTO;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.entity.BankEntity;
import api.bank.domain.usecase.DeleteConsultationHistoryUseCase;
import api.bank.domain.usecase.ProcessCsvUploadUseCase;
import api.bank.domain.usecase.UploadCsvUseCase;
import api.security.auth.app.exception.UserNotFoundException;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.usecase.SearchUserByEmailUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProcessCsvUploadService implements ProcessCsvUploadUseCase {

    private final DeleteConsultationHistoryUseCase deleteConsultationHistoryUseCase;
    private final SearchUserByEmailUseCase searchUserByEmailUseCase;
    private final UploadCsvUseCase uploadCsvUseCase;
    private final BankDataProvider bankDataProvider;

    @Override
    public String execute(MultipartFile file, String email, String bankName) {

        BankEntity bankEntity = this.bankDataProvider.findByName(bankName);
        this.deleteConsultationHistoryUseCase.execute(email, bankName);

        UserEntity user = this.searchUserByEmailUseCase.execute(email);
        if (user == null) {
            throw new UserNotFoundException("User with the provided email was not found: " + email);
        }

        UploadCsvDTO dto = new UploadCsvDTO();
        dto.setFile(file);
        dto.setEmail(email);
        dto.setUserDocument(user.getDocument());
        dto.setBankId(bankEntity.getId());

        return this.uploadCsvUseCase.execute(dto);
    }
}