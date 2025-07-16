package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.entity.ExecutorEntity;
import api.bank.domain.usecase.DeleteConsultationHistoryUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DeleteConsultationHistoryService implements DeleteConsultationHistoryUseCase {
    
    private final ExecutorDataProvider executorDataProvider;
    private final ConsultationEventsDataProvider consultationEventsDataProvider;
    
    @Override
    public void execute(String email, String bankId) {

        Boolean executorFind = this.executorDataProvider.existsByEmailAndBankId(email, bankId);

        if (executorFind) {
            ExecutorEntity entity = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(email, bankId);
            
            this.consultationEventsDataProvider.deleteByCsvId(entity.getId());
            this.executorDataProvider.deleteById(entity.getId());
        }
    }

}
