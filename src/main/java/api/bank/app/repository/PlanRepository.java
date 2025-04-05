package api.bank.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    
    
}
