package api.bank.app.entrypoints;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.app.restmodel.ProcessamentoCsvRestModel;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.usecase.SearchUserByEmailUseCase;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/queries")
@CrossOrigin
@AllArgsConstructor
public class ConsultationEventsController implements ConsultationEventsResource {

    private final ConsultationEventsRepository repository;
    private final CsvProcessManagerUseCase csvProcessManager;
    private final LogSenderUseCase logSender;
    private final SearchUserByEmailUseCase searchUserByEmailUseCase;

    @Override
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file, @RequestHeader("email")String email) {
        try {
            String csvId = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();

            UserEntity user = this.searchUserByEmailUseCase.execute(email);

            List<ConsultationEvents> registros = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(document -> {
                        ConsultationEvents ce = new ConsultationEvents();
                        ce.setDocumentClient(document.replaceAll("[\".\\-\\s]", "").trim());
                        ce.setCsvId(csvId);
                        ce.setInsertionDate(now);
                        ce.setValueResult(null); 
                        ce.setUser(user.getDocument());
                        return ce;
                    })
                    .collect(Collectors.toList());

            repository.saveAll(registros);

            return ResponseEntity.ok(csvId);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar o CSV: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/executar")
    public ResponseEntity<String> processarCsv(@RequestBody ProcessamentoCsvRestModel request) {
        String processoId = csvProcessManager.iniciarProcessamento(
            request.getCsvId(),
            request.getUsuarios(),
            logSender
        );
        return ResponseEntity.ok(processoId);
    }   

    @PostMapping("/parar/{processoId}")
    public ResponseEntity<String> pararProcessamento(@PathVariable String processoId) {
        boolean parado = csvProcessManager.pararProcessamento(processoId);
        if (parado) {
            return ResponseEntity.ok("Processo " + processoId + " interrompido com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo não encontrado ou já finalizado.");
        }
    }


}
