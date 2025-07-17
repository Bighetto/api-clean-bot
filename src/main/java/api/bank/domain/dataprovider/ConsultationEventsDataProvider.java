package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.ResultsCountProjection;
import api.bank.domain.entity.ConsultationEventsEntity;

public interface ConsultationEventsDataProvider {

    ResultsCountProjection getResultsCounterByCsvId(String executorId);

    Integer findQuantityOfDocuments(String executorId);

    void deleteByCsvId(String id);
    
    void saveAll(List<ConsultationEvents> models);

    List<ConsultationEventsEntity> findAllByCsvId(String id);
}
