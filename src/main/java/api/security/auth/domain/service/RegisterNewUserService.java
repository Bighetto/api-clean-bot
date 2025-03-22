package api.security.auth.domain.service;

import org.springframework.stereotype.Component;

import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.usecase.RegisterNewUserUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RegisterNewUserService implements RegisterNewUserUseCase{
    
    private final AuthDataProvider provider;

    @Override
    public void execute(UserLogin model) {
        this.provider.saveNewUser(model);
    }
    
}
