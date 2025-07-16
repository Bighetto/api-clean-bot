package api.bank.app.entrypoints;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.exception.UploadCsvExecption;
import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.Executor;
import api.bank.app.model.ResultsCountProjection;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.app.repository.ExecutorRepository;
import api.bank.app.restmodel.FindCsvStatusRestModel;
import api.bank.app.restmodel.ProcessamentoCsvRestModel;
import api.bank.app.restmodel.ResultsCounterExecutionRestModel;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.DeleteConsultationHistoryUseCase;
import api.bank.domain.usecase.FileExportGenerationUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import api.bank.domain.usecase.ProcessCsvUploadUseCase;
import api.security.auth.app.exception.UserNotFoundException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/queries")
@CrossOrigin
@AllArgsConstructor
public class ConsultationEventsController implements ConsultationEventsResource {

    private final ConsultationEventsRepository repository;
    private final CsvProcessManagerUseCase csvProcessManager;
    private final LogSenderUseCase logSender;
    private final ExecutorRepository executorRepository;
    private final FileExportGenerationUseCase fileExportGenerationUseCase;
    private final ProcessCsvUploadUseCase processCsvUploadUseCase;
    private final DeleteConsultationHistoryUseCase deleteConsultationHistoryUseCase;

    @Override
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file,
            @RequestHeader("email") String email, @RequestHeader("bankId")String bankId) {
        try {
            String csvId = this.processCsvUploadUseCase.execute(file, email, bankId);

            return ResponseEntity.ok(Map.of("csvId", csvId)); 
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (UploadCsvExecption e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro interno no sistema."));
        } 
    }

    @Override
    @PostMapping("/executar")
    public ResponseEntity<Map<String, String>> processarCsv(@RequestBody ProcessamentoCsvRestModel request) {
        try {

            Optional<Executor> executor = this.executorRepository.findCurrentProcessStatusByEmail(request.getEmail());

            if (executor.isEmpty() || executor.get().getProcessStatus() == ProcessStatus.EM_ANDAMENTO) {
                return ResponseEntity.badRequest().build();
            }
            String processoId = csvProcessManager.iniciarProcessamento(
                    request.getCsvId(),
                    request.getUsuarios(),
                    request.getEmail(),
                    logSender);

            return ResponseEntity.ok(Map.of("processoId", processoId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/parar/{email}")
    public ResponseEntity<String> pararProcessamento(@PathVariable String email) {
        Optional<Executor> executor = this.executorRepository.findCurrentProcessStatusByEmail(email);
        if (executor.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        boolean parado = csvProcessManager.pararProcessamento(executor.get().getProcessId());

        if (parado) {
            return ResponseEntity.ok("Processo interrompido com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo não encontrado ou já finalizado.");
        }
    }

    @Override
    @GetMapping("/status/{email}")
    public ResponseEntity<FindCsvStatusRestModel> buscarStatusAtualProcessamentoCsv(
            @PathVariable("email") String email) {

        Optional<Executor> currentStatus = this.executorRepository.findCurrentProcessStatusByEmail(email);

        if (currentStatus.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ResultsCountProjection result = this.repository.getResultsCounterByCsvId(currentStatus.get().getId());
        Integer quantityDocuments = this.repository.findQuantityOfDocuments(currentStatus.get().getId());

        FindCsvStatusRestModel restModel = new FindCsvStatusRestModel();
        restModel.setQuantidadeDocumentos(quantityDocuments);
        restModel.setIdCsv(currentStatus.get().getId());
        restModel.setStatus(currentStatus.get().getProcessStatus().toString());

        ResultsCounterExecutionRestModel resultsRestModel = new ResultsCounterExecutionRestModel();
        resultsRestModel.setTotal_consultas(result.getTotal_consultas());
        resultsRestModel.setCom_saldo(result.getCom_saldo());
        resultsRestModel.setErro(result.getErro());
        resultsRestModel.setNao_autorizado(result.getNao_autorizado());
        resultsRestModel.setSem_saldo(result.getSem_saldo());

        restModel.setResults(resultsRestModel);

        return ResponseEntity.ok().body(restModel);

    }

    @Override
    @DeleteMapping(value = "/zerar-resultados")
    public ResponseEntity<String> zerarResultados(
            @RequestHeader("email") String email, 
            @RequestHeader("bankId")String bankId) {

        try {
            Boolean result = this.deleteConsultationHistoryUseCase.execute(email, bankId); 
            
            if(result != true) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registros de consulta não encontrados.");
            }

            return ResponseEntity.ok().body("Dados zerados com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no sistema.");
        }
    }

    @Override
    @PostMapping("/export/{email}")
    public ResponseEntity<byte[]> exportarResultados(@PathVariable("email") String email) {

        Optional<Executor> executorFind = executorRepository.findCurrentProcessStatusByEmail(email);

        if (executorFind.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<ConsultationEvents> events = repository.findAllByCsvId(executorFind.get().getId());

        List<String[]> data = events.stream()
                .map(e -> new String[] {
                        e.getDocumentClient(),
                        e.getValueResult()
                })
                .toList();

        String[] headers = { "Documento", "Resultado" };

        try {
            ByteArrayInputStream stream = this.fileExportGenerationUseCase.execute(data, headers);
            byte[] bytes = stream.readAllBytes();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resultado-consultas.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(bytes);
        } catch (IOException e1) {
            return ResponseEntity.internalServerError().build();
        }

    }

}
