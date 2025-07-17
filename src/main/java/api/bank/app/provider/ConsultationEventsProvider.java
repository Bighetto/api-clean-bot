package api.bank.app.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import api.bank.app.converter.ConsultationEventsModelToEntityConverter;
import api.bank.app.exception.ConsultationEventsNotFoundException;
import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.ResultsCountProjection;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.entity.ConsultationEventsEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConsultationEventsProvider implements ConsultationEventsDataProvider {

    private final ConsultationEventsRepository consultationEventsRepository;
    private final ConsultationEventsModelToEntityConverter consultationEventsModelToEntityConverter;

    @Override
    public void deleteByCsvId(String id) {
        this.consultationEventsRepository.deleteByCsvId(id);
    }

    @Override
    public void saveAll(List<ConsultationEvents> models) {
        this.consultationEventsRepository.saveAll(models);
    }

    @Override
    public ResultsCountProjection getResultsCounterByCsvId(String executorId) {
        return this.consultationEventsRepository.getResultsCounterByCsvId(executorId);
    }

    @Override
    public Integer findQuantityOfDocuments(String executorId) {
        return this.consultationEventsRepository.findQuantityOfDocuments(executorId);
    }

    @Override
    public List<ConsultationEventsEntity> findAllByCsvId(String id) {
        List<ConsultationEvents> modelsList = this.consultationEventsRepository.findAllByCsvId(id);

        if (modelsList.isEmpty()) {
            throw new ConsultationEventsNotFoundException("Nenhum registro de consulta encontrado no CSV utilizado");
        }

        return modelsList.stream()
            .map(model -> consultationEventsModelToEntityConverter.convertToEntity(model))
            .collect(Collectors.toList());
    }
}
