package api.bank.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.ResultsCountProjection;

@Repository
public interface ConsultationEventsRepository extends JpaRepository<ConsultationEvents, String> {

    List<ConsultationEvents> findByCsvId_Id(String csvId);

    @Query(value = """
    SELECT
      COALESCE(total_consultas, 0) AS total_consultas,
      COALESCE(nao_autorizado, 0) AS nao_autorizado,
      COALESCE(erro, 0) AS erro,
      COALESCE(sem_saldo, 0) AS sem_saldo,
      COALESCE(com_saldo, 0) AS com_saldo
    FROM (
      SELECT
        COUNT(*) AS total_consultas,
        SUM(CASE WHEN value_result = 'NÃO AUTORIZADO' THEN 1 ELSE 0 END) AS nao_autorizado,
        SUM(CASE WHEN value_result IN ('CPF INVÁLIDO', 'ERRO NA REQUISIÇÃO') THEN 1 ELSE 0 END) AS erro,
        SUM(CASE WHEN value_result = 'SEM SALDO' THEN 1 ELSE 0 END) AS sem_saldo,
        SUM(CASE WHEN value_result REGEXP '^[0-9]+,[0-9]{2}$' THEN 1 ELSE 0 END) AS com_saldo
      FROM queries_tb
      WHERE value_result != ''
        AND csv_id = :csvId
    ) AS resultado
    """, nativeQuery = true)
ResultsCountProjection getResultsCounterByCsvId(@Param("csvId") String csvId);


}
