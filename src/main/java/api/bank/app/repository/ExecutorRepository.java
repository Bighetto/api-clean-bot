package api.bank.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.Executor;

@Repository
public interface ExecutorRepository extends JpaRepository<Executor, String>{
    
}
