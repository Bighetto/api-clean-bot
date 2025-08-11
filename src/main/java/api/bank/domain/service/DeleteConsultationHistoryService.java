package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.domain.dataprovider.BankDataProvider;
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
    private final BankDataProvider bankDataProvider;
    
    @Override
    public Boolean execute(String email, String bankName) {
        String bankId = this.bankDataProvider.findByName(bankName).getId();

        Boolean executorFind = this.executorDataProvider.existsByEmailAndBankId(email, bankId);

        if (executorFind) {
            ExecutorEntity entity = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(email, bankId);
            
            this.consultationEventsDataProvider.deleteByCsvId(entity.getId());
            this.executorDataProvider.deleteById(entity.getId());
        }

        return executorFind;
    }

}
