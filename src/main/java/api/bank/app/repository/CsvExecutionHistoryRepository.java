package api.bank.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import api.bank.app.model.CsvExecutionHistory;

public interface CsvExecutionHistoryRepository extends JpaRepository<CsvExecutionHistory, String> {

}
