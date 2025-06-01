package api.bank.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.model.Executor;
import jakarta.transaction.Transactional;

@Repository
public interface ExecutorRepository extends JpaRepository<Executor, String>{

    @Query(value = " select qe.* from query_executor qe " + 
                " join user_login ul " + 
                " on ul.email  = qe.user_login " + 
                " where ul.email =  :email ", nativeQuery = true)
    Optional<Executor> findCurrentProcessStatusByEmail(@Param("email")String email);
    
    @Transactional
    @Modifying
    @Query(" UPDATE Executor e SET e.processStatus = :status WHERE e.id = :csvId ")
    void updateProcessStatusByCsvId(@Param("csvId") String csvId, @Param("status") ProcessStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE Executor p SET p.processStatus = :statusPendente WHERE p.processStatus = :statusEmAndamento")
    void updateAllStatusEmAndamentoToPendente(@Param("statusPendente") ProcessStatus statusPendente, @Param("statusEmAndamento") ProcessStatus statusEmAndamento);

    
}
