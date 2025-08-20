package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.Executor;
import api.bank.domain.entity.ExecutorEntity;
import api.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExecutorModelToEntityConverter extends ConvertCase<ExecutorEntity, Executor> {

    @Override
    public Executor convertToModel(ExecutorEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public ExecutorEntity convertToEntity(Executor model) {
        return ExecutorEntity.builder()
        .id(model.getId())
        .userLogin(model.getUserLogin())
        .processStatus(model.getProcessStatus())
        .totalRegistered(model.getTotalRegistered())
        .processId(model.getProcessId())
        .bankId(model.getBank().getId())
        .build();
    }

}
