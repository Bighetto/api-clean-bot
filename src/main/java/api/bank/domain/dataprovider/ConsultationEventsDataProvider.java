package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.ResultsCountProjection;

public interface ConsultationEventsDataProvider {

    ResultsCountProjection getResultsCounterByCsvId(String executorId);

    Integer findQuantityOfDocuments(String executorId);

    void deleteByCsvId(String id);
    
    void saveAll(List<ConsultationEvents> models);
}
