package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.ConsultationEvents;
import api.bank.domain.entity.ConsultationEventsEntity;
import api.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConsultationEventsModelToEntityConverter extends ConvertCase<ConsultationEventsEntity, ConsultationEvents> {

    @Override
    public ConsultationEvents convertToModel(ConsultationEventsEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToModel'");
    }

    @Override
    public ConsultationEventsEntity convertToEntity(ConsultationEvents model) {
        return ConsultationEventsEntity.builder()
            .id(model.getId())
            .documentClient(model.getDocumentClient())
            .valueResult(model.getValueResult())
            .bankId(model.getBank().getId())
            .build();
    }

}
