package api.security.auth.app.provider;

import java.util.Optional;

import org.springframework.stereotype.Component;

import api.security.auth.app.converter.UserModelToEntityConverter;
import api.security.auth.app.model.UserLogin;
import api.security.auth.app.repository.UserRepository;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import api.security.auth.domain.entity.UserEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthProvider implements AuthDataProvider {

    private final UserRepository userRepository;
    private final UserModelToEntityConverter userModelToEntityConverter;

    @Override
    public void saveNewUser(UserLogin model) {
        this.userRepository.save(model);
    }

    @Override
    public UserEntity findByDocument(String document) {

        Optional<UserLogin> model = this.userRepository.findById(document);

        if (model.isEmpty()) {
            throw new RuntimeException();
        }

        return this.userModelToEntityConverter.convertToEntity(model.get());
    }

    @Override
    public UserEntity findByEmail(String email) {
        Optional<UserLogin> model = Optional.ofNullable(this.userRepository.findByEmail(email));

        if (model.isEmpty()) {
            return null;
        }

        return this.userModelToEntityConverter.convertToEntity(model.get());
    }

    @Override
    public void updatePassword(String document, String password) {
        
        Optional<UserLogin> optionalUser = userRepository.findById(document);
        if (optionalUser.isPresent()) {
            UserLogin user = optionalUser.get();
            user.setPassword(password);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void deleteUserByEmail(String email) {
        
        this.userRepository.deleteByEmail(email);
    }

    @Override
    public void inativateUserByEmail(String email) {
        
        this.userRepository.setUserInactiveByEmail(email);
    }

    @Override
    public void ativateUserByEmail(String email) {
        this.userRepository.setUserActiveByEmail(email);
    }
    
}
