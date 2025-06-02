package api.security.auth.domain.dataprovider;

import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.entity.UserEntity;

public interface AuthDataProvider {

    void saveNewUser(UserLogin model);

    UserEntity findByDocument(String document);

    UserEntity findByEmail(String email);

    void updatePassword(String document, String password);

    void deleteUserByEmail(String email);

    void inativateUserByEmail(String email);

    void ativateUserByEmail(String email);
    
}
