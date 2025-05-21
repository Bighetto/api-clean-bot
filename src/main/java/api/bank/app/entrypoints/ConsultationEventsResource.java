package api.bank.app.entrypoints;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.restmodel.FindCsvStatusRestModel;
import api.bank.app.restmodel.ProcessamentoCsvRestModel;

public interface ConsultationEventsResource {

    public ResponseEntity<Map<String, String>> uploadCsv(MultipartFile file, String email);

    public ResponseEntity<String> processarCsv(ProcessamentoCsvRestModel request);

    public ResponseEntity<FindCsvStatusRestModel> buscarStatusAtualProcessamentoCsv(String email);

}
