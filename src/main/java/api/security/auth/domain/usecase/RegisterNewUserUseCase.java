package api.security.auth.domain.usecase;

import api.security.auth.app.model.UserLogin;

public interface RegisterNewUserUseCase {
    
    void execute(UserLogin model);
}
