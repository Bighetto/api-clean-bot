package api.security.auth.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.security.auth.app.model.Token;
import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findAllByUserEmail(String userEmail);

    @Transactional
    void deleteByUserEmail(String userEmail);
}
