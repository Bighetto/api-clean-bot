package api.bank.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import api.bank.app.converter.PlanModelToEntityConverter;
import api.bank.app.model.Plan;
import api.bank.app.provider.PlanProvider;
import api.bank.app.repository.PlanRepository;
import api.bank.domain.entity.PlanEntity;
import api.security.auth.app.model.UserLogin;

@ExtendWith(MockitoExtension.class)
public class PlanDataProviderTest {

    @Mock
    private PlanRepository planRepository;
    @Mock
    private PlanModelToEntityConverter converter;

    PlanDataProvider dataProvider;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        dataProvider = new PlanProvider(planRepository, converter);
    }

    @Test
    void shoudReturnSuccessWhenPlanIsFound(){

        Plan plan = new Plan();
        plan.setId(UUID.randomUUID().toString());
        plan.setName("v8");
        plan.setPrice(14.99);

        PlanEntity entity = new PlanEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setName("v8");
        entity.setPrice(14.99);

        when(planRepository.findById(anyString())).thenReturn(Optional.of(plan));
        when(converter.convertToEntity(any())).thenReturn(entity);

        PlanEntity result = this.dataProvider.findPlanById("test");

        assertEquals(14.99, result.getPrice());
        assertNotNull(result);
        assertEquals("v8", result.getName());
    }


    @Test
    void shouldThrowRuntimeExceptionWhenNoPlanIsFound() {


        when(planRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dataProvider.findPlanById("teste"));

    }
}
