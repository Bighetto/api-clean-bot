package api.bank.domain.usecase;

public interface UpdateBankUserNicknameUseCase {
    void execute(String bankUserId, String newNickname);
}
