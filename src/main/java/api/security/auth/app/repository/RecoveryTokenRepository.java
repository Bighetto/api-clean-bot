package api.security.auth.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.security.auth.app.model.RecoveryToken;
import jakarta.transaction.Transactional;

@Repository
public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, String> {

    RecoveryToken findByUserEmail(String userEmail);

    @Transactional
    void deleteByUserEmail(String userEmail);

    RecoveryToken findByToken(String token);
}
