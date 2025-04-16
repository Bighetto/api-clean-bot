package api.bank.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.bank.app.model.ConsultationEvents;

@Repository
public interface ConsultationEventsRepository extends JpaRepository<ConsultationEvents, String> {

}
