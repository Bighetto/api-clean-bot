package api.security.auth.domain.service;

import org.springframework.stereotype.Component;

import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.usecase.UpdatePasswordUserLoginUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UpdatePasswordUserLoginService implements UpdatePasswordUserLoginUseCase{

    private final AuthDataProvider authDataProvider;

    @Override
    public void execute(String document, String encryptedPassword) {

        authDataProvider.updatePassword(document, encryptedPassword);
        
    }
    
}
