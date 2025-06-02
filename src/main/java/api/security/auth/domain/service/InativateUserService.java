package api.security.auth.domain.service;

import org.springframework.stereotype.Service;

import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.usecase.InativateUserUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InativateUserService implements InativateUserUseCase{

    private final AuthDataProvider dataprovider;

    @Override
    public void execute(String email) {
        
        this.dataprovider.inativateUserByEmail(email);
    }
    
}
