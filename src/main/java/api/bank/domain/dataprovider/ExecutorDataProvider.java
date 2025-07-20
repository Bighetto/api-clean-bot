package api.bank.domain.dataprovider;

import api.bank.app.model.Executor;
import api.bank.domain.entity.ExecutorEntity;

public interface ExecutorDataProvider {
    
    Boolean existsByEmailAndBankId(String email, String bankId);
    
    ExecutorEntity findCurrentProcessStatusByEmailAndBankId(String email, String bankId);

    void updateProcessIdByCsvId(String csvId, String processId);

    void updateProcessStatusByCsvId(String csvId, String statusName);

    void deleteById(String id);

    void save(Executor executor);
}
