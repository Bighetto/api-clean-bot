package api.bank.app.provider;

import java.util.Optional;

import org.springframework.stereotype.Component;

import api.bank.app.converter.PlanModelToEntityConverter;
import api.bank.app.model.Plan;
import api.bank.app.repository.PlanRepository;
import api.bank.domain.dataprovider.PlanDataProvider;
import api.bank.domain.entity.PlanEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PlanProvider implements PlanDataProvider{

    private final PlanRepository planRepository;
    private final PlanModelToEntityConverter converter;

    @Override
    public PlanEntity findPlanById(String id) {
        
        Optional<Plan> plan = planRepository.findById(id);

        if (plan.isEmpty()) {
            throw new RuntimeException();
        }

        return this.converter.convertToEntity(plan.get());
    }
    
}
