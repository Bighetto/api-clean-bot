package api.bank.app.provider;

import java.util.List;

import org.springframework.stereotype.Component;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConsultationEventsProvider implements ConsultationEventsDataProvider {

    private final ConsultationEventsRepository consultationEventsRepository;

    @Override
    public void deleteByCsvId(String id) {
        this.consultationEventsRepository.deleteByCsvId(id);
    }

    @Override
    public void saveAll(List<ConsultationEvents> models) {
        this.consultationEventsRepository.saveAll(models);
    }
}
