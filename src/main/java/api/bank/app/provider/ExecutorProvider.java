package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.app.converter.ExecutorModelToEntityConverter;
import api.bank.app.exception.ExecutorNotFoundException;
import api.bank.app.exception.UpdateProcessIdException;
import api.bank.app.exception.UpdateProcessStatusException;
import api.bank.app.model.Executor;
import api.bank.app.repository.ExecutorRepository;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.entity.ExecutorEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExecutorProvider implements ExecutorDataProvider {

    private final ExecutorModelToEntityConverter executorModelToEntityConverter;
    private final ExecutorRepository executorRepository;

    @Override
    public Boolean existsByEmailAndBankId(String email, String bankId) {
        Integer result = this.executorRepository.existsByEmailAndBankId(email, bankId);
        return result != null && result == 1;
    }

    @Override
    public ExecutorEntity findCurrentProcessStatusByEmailAndBankId(String email, String bankId) {
        return this.executorModelToEntityConverter.convertToEntity(
            executorRepository.findCurrentProcessStatusByEmailAndBankId(email, bankId)
            .orElseThrow(() -> new ExecutorNotFoundException("Nenhum registro de consulta foi encontrado para o usuario do e-mail: " + email)));
    }

    @Override
    public void deleteById(String id) {
        this.executorRepository.deleteById(id);
    }

    public void save(Executor executor) {
        this.executorRepository.save(executor);
    }

    @Override
    public void updateProcessIdByCsvId(String csvId, String processId) {
        this.executorRepository.findById(csvId)
            .orElseThrow(() -> new UpdateProcessIdException("Registro não encontrado"));
        
        try {
            this.executorRepository.updateProcessIdByCsvId(csvId, processId);   
        } catch (Exception e) {
            throw new UpdateProcessIdException("Erro ao atualizar o status do CSV: " + csvId);
        }
    }

    @Override
    public void updateProcessStatusByCsvId(String csvId, String statusName) {
        this.executorRepository.findById(csvId)
            .orElseThrow(() -> new UpdateProcessStatusException("Registro não encontrado"));
        
        try {
            this.executorRepository.updateProcessStatusByCsvId(csvId, statusName);
        } catch (Exception e) {
            throw new UpdateProcessStatusException("Erro ao atualizar o status do CSV: " + csvId);
        }
    }
}
