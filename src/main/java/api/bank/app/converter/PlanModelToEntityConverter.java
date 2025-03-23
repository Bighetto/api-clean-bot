package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.Plan;
import api.bank.domain.entity.PlanEntity;
import api.security.auth.domain.utils.abstractClasses.ConvertCase;

@Component
public class PlanModelToEntityConverter extends ConvertCase<PlanEntity, Plan>{

    @Override
    public Plan convertToModel(PlanEntity entity) {

        return new Plan(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getLimitUsers(),
            entity.getLimitBanks(),
            entity.getLimitRequests()
            );
    
    }

    @Override
    public PlanEntity convertToEntity(Plan model) {
        
        return new PlanEntity(
            model.getId(),
            model.getName(),
            model.getPrice(),
            model.getLimitUsers(),
            model.getLimitBanks(),
            model.getLimitRequests()
            );
    }

    
}
