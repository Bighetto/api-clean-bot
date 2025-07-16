package api.bank.domain.usecase;

public interface DeleteConsultationHistoryUseCase {
    void execute(String email, String bankId);
}
