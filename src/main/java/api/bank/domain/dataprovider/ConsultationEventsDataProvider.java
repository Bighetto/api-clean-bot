package api.bank.domain.dataprovider;

import java.util.List;

import api.bank.app.model.ConsultationEvents;

public interface ConsultationEventsDataProvider {

    void deleteByCsvId(String id);
    
    void saveAll(List<ConsultationEvents> models);
}
