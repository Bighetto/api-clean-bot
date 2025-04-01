package api.security.auth.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.security.auth.app.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

}
