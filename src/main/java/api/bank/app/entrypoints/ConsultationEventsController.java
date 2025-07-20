package api.bank.app.entrypoints;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.converter.FindCsvStatusDTOToRestModelConverter;
import api.bank.app.converter.ProcessamentoCsvRestModelToDtoConverter;
import api.bank.app.exception.ConflictException;
import api.bank.app.exception.ConsultationEventsNotFoundException;
import api.bank.app.exception.ExecutorNotFoundException;
import api.bank.app.exception.FileExportException;
import api.bank.app.exception.StopExecutionException;
import api.bank.app.exception.UpdateProcessIdException;
import api.bank.app.exception.UpdateProcessStatusException;
import api.bank.app.exception.UploadCsvException;
import api.bank.app.restmodel.FindCsvStatusRestModel;
import api.bank.app.restmodel.ProcessamentoCsvRestModel;
import api.bank.domain.dto.ProcessamentoCsvDTO;
import api.bank.domain.usecase.CsvStatusCheckerUseCase;
import api.bank.domain.usecase.DeleteConsultationHistoryUseCase;
import api.bank.domain.usecase.ExportConsultationResultsUseCase;
import api.bank.domain.usecase.ProcessCsvUploadUseCase;
import api.bank.domain.usecase.StartCsvProcessingUseCase;
import api.bank.domain.usecase.StopExecutionUseCase;
import api.security.auth.app.exception.UserNotFoundException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/queries")
@CrossOrigin
@AllArgsConstructor
public class ConsultationEventsController implements ConsultationEventsResource {

    private final ProcessCsvUploadUseCase processCsvUploadUseCase;
    private final DeleteConsultationHistoryUseCase deleteConsultationHistoryUseCase;
    private final CsvStatusCheckerUseCase csvStatusCheckerUseCase;
    private final FindCsvStatusDTOToRestModelConverter findCsvStatusDTOToRestModelConverter;
    private final ExportConsultationResultsUseCase exportConsultationResultsUseCase;
    private final StopExecutionUseCase stopExecutionUseCase;
    private final ProcessamentoCsvRestModelToDtoConverter processamentoCsvRestModelToDtoConverter;
    private final StartCsvProcessingUseCase startCsvProcessingUseCase;

    @Override
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file,
            @RequestHeader("email") String email, @RequestHeader("bankId") String bankId) {
        try {
            String csvId = this.processCsvUploadUseCase.execute(file, email, bankId);

            return ResponseEntity.ok(Map.of("csvId", csvId)); 
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (UploadCsvException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro interno no sistema."));
        } 
    }

    @Override
    @PostMapping("/executar")
    public ResponseEntity<Map<String, String>> processarCsv(@RequestBody ProcessamentoCsvRestModel request) {
        try {
            ProcessamentoCsvDTO processamentoCsvDTO = this.processamentoCsvRestModelToDtoConverter.convertToDto(request);

            String processoId = this.startCsvProcessingUseCase.execute(processamentoCsvDTO);

            return ResponseEntity.ok(Map.of("processoId", processoId));
        } catch (ConflictException | UpdateProcessIdException | UpdateProcessStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Erro: ", e.getMessage()));
        } catch(ConsultationEventsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Erro: ", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/parar")
    public ResponseEntity<String> pararProcessamento(
            @RequestHeader("email") String email, 
            @RequestHeader("bankId")String bankId) {

        try {
            boolean parado = stopExecutionUseCase.execute(email, bankId);
            
            if (parado) {
                return ResponseEntity.ok("Processo interrompido com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo já finalizado.");
            }
        } catch (ExecutorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(StopExecutionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no sistema, por favor tente novamente");
        }
    }

    @Override
    @GetMapping("/status")
    public ResponseEntity<FindCsvStatusRestModel> buscarStatusAtualProcessamentoCsv(
            @RequestHeader("email") String email, 
            @RequestHeader("bankId")String bankId) {

        try {
            FindCsvStatusRestModel statusInfo = this.findCsvStatusDTOToRestModelConverter
                .convertToModel(this.csvStatusCheckerUseCase.execute(email, bankId));
            
            return ResponseEntity.ok().body(statusInfo);    
        } catch (ExecutorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
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
    @PostMapping("/export")
    public ResponseEntity<?> exportarResultados(
            @RequestHeader("email") String email, 
            @RequestHeader("bankId")String bankId) {

        try {
            byte[] bytes = this.exportConsultationResultsUseCase.execute(email, bankId);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resultado-consultas.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(bytes);
        } catch (ExecutorNotFoundException | ConsultationEventsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FileExportException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
