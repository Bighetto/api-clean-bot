package api.bank.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import api.bank.app.model.Executor;

@Repository
public interface ExecutorRepository extends JpaRepository<Executor, String>{

    @Query(value = " select qe.* from query_executor qe " + 
                " join user_login ul " + 
                " on ul.email  = qe.user_login " + 
                " where ul.email =  :email ", nativeQuery = true)
    Optional<Executor> findCurrentProcessStatusByEmail(@Param("email")String email);
    
}
