package api.bank.domain.usecase;

public interface ExportConsultationResultsUseCase {
    byte[] execute(String email, String bankName);
}
