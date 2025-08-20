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
public interface ExecutorRepository extends JpaRepository<Executor, String> {

    @Query(value = " select qe.* from query_executor qe " +
            " join user_login ul " +
            " on ul.email  = qe.user_login " +
            " where ul.email =  :email ", nativeQuery = true)
    Optional<Executor> findCurrentProcessStatusByEmail(@Param("email") String email);

    @Query(value = "SELECT qe.* FROM query_executor qe " +
               "WHERE qe.user_login = :email " +
               "AND qe.bank_id = :bankId", nativeQuery = true)
    Optional<Executor> findCurrentProcessStatusByEmailAndBankId(
        @Param("email") String email, 
        @Param("bankId") String bankId
    );

    @Transactional
    @Modifying
    @Query(value = " UPDATE query_executor e SET e.process_status = :status WHERE e.id = :csvId ", nativeQuery = true)
    void updateProcessStatusByCsvId(@Param("csvId") String csvId, @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = " UPDATE query_executor e SET e.process_id = :processId WHERE e.id = :csvId ", nativeQuery = true)
    void updateProcessIdByCsvId(@Param("csvId") String csvId, @Param("processId") String processId);

    @Transactional
    @Modifying
    @Query("UPDATE Executor p SET p.processStatus = :statusPendente WHERE p.processStatus = :statusEmAndamento")
    void updateAllStatusEmAndamentoToPendente(@Param("statusPendente") ProcessStatus statusPendente,
            @Param("statusEmAndamento") ProcessStatus statusEmAndamento);

    @Query(value = "SELECT EXISTS(" +
                "  SELECT 1 FROM query_executor qe " +
                "  JOIN user_login ul ON ul.email = qe.user_login " +
                "  WHERE ul.email = :email AND qe.bank_id = :bankId" +
                ")", nativeQuery = true)
    Integer existsByEmailAndBankId(@Param("email") String email, @Param("bankId") String bankId);   
}
