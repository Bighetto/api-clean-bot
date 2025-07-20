package api.bank.domain.dataprovider;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.ResultsCountProjection;
import api.bank.domain.dto.UpdateValueResultDTO;
import api.bank.domain.entity.ConsultationEventsEntity;

public interface ConsultationEventsDataProvider {

    ResultsCountProjection getResultsCounterByCsvId(String executorId);

    Integer findQuantityOfDocuments(String executorId);

    void deleteByCsvId(String id);
    
    void saveAll(List<ConsultationEvents> models);

    List<ConsultationEventsEntity> findAllByCsvId(String id);

    Page<ConsultationEventsEntity> findValidByCsvId(String csvId, Pageable pageable);

    void updateValueResult(UpdateValueResultDTO dto);

    Integer countResultsNull(String csvId);
}
