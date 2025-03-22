package api.security.auth.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.security.auth.app.model.UserLogin;

@Repository
public interface UserRepository extends JpaRepository<UserLogin, String>{

    Optional<UserLogin> findById(String id);

    UserLogin findByEmail(String email);
    
}
