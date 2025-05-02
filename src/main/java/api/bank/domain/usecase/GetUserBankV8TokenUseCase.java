package api.bank.domain.usecase;

public interface GetUserBankV8TokenUseCase {
    String execute(String login, String password);
}
