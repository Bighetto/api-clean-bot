package api.security.auth.domain.usecase;

public interface InativateUserUseCase {
    
    void execute(String email);
}
