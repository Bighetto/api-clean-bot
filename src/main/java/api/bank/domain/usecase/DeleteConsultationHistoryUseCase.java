package api.bank.domain.usecase;

public interface DeleteConsultationHistoryUseCase {
    Boolean execute(String email, String bankId);
}
