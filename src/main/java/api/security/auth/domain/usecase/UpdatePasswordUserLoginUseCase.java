package api.security.auth.domain.usecase;

public interface UpdatePasswordUserLoginUseCase {

    void execute(String document, String encryptedPassword);
    
}
