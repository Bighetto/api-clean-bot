package api.security.auth.domain.usecase;

public interface SendRecoverUserPasswordEmailUseCase {

    void execute(String name, String to, String token);
}
