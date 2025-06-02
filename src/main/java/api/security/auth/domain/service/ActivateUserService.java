package api.security.auth.domain.service;

import org.springframework.stereotype.Service;

import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.usecase.ActivateUserUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActivateUserService implements ActivateUserUseCase{
    
    private final AuthDataProvider authDataProvider;
    
    @Override
    public void execute(String email) {

        this.authDataProvider.ativateUserByEmail(email);
        
    }
    
}
