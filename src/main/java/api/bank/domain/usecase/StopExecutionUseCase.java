package api.bank.domain.usecase;

public interface StopExecutionUseCase {
    Boolean execute(String email, String bankId);
}
