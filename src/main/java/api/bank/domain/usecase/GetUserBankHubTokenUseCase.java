package api.bank.domain.usecase;

public interface GetUserBankHubTokenUseCase {
    String execute(String login, String password);
}
