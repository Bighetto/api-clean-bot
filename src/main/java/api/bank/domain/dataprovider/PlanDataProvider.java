package api.bank.domain.dataprovider;

import api.bank.domain.entity.PlanEntity;
public interface PlanDataProvider {

    PlanEntity findPlanById(String id);
    
}
