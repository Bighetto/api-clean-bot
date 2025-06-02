package api.security.auth.domain.service;

import org.springframework.stereotype.Service;

import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.usecase.DeleteUserUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteUserService implements DeleteUserUseCase{

    private final AuthDataProvider authDataProvider;

    @Override
    public void execute(String email) {
        
        this.authDataProvider.deleteUserByEmail(email);
        
    }
    
}
