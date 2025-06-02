package api.security.auth.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import api.security.auth.app.model.UserLogin;
import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserLogin, String>{

    Optional<UserLogin> findById(String id);

    UserLogin findByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserLogin u SET u.status = 'INATIVO' WHERE u.email = :email")
    int setUserInactiveByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserLogin u SET u.status = 'ATIVO' WHERE u.email = :email")
    int setUserActiveByEmail(String email);
    
}
