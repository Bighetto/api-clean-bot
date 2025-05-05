package api.bank.app.entrypoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.restmodel.ProcessamentoCsvRestModel;

public interface ConsultationEventsResource {

    public ResponseEntity<String> uploadCsv(MultipartFile file, String email);

    public ResponseEntity<String> processarCsv(ProcessamentoCsvRestModel request);

}
