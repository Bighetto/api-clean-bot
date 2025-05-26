package api.bank.app.entrypoints;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.Executor;
import api.bank.app.model.ResultsCountProjection;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.app.repository.ExecutorRepository;
import api.bank.app.restmodel.FindCsvStatusRestModel;
import api.bank.app.restmodel.ProcessamentoCsvRestModel;
import api.bank.app.restmodel.ResultsCounterExecutionRestModel;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.FileExportGenerationUseCase;
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
    private final ExecutorRepository executorRepository;
    private final FileExportGenerationUseCase fileExportGenerationUseCase;

    @Override
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file, @RequestHeader("email")String email) {
        try {

            Optional<Executor> executorFind = executorRepository.findCurrentProcessStatusByEmail(email);

            if (executorFind.isPresent()) {
                repository.deleteByCsvId(executorFind.get().getId());
                executorRepository.deleteById(executorFind.get().getId());
            }

            String csvId = UUID.randomUUID().toString();
            Executor executor = new Executor();
            executor.setId(csvId); 
            executor.setUserLogin(email);
            executor.setProcessStatus(ProcessStatus.PENDENTE);
            LocalDateTime now = LocalDateTime.now();

            UserEntity user = this.searchUserByEmailUseCase.execute(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            executorRepository.save(executor);

            List<ConsultationEvents> registros = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(document -> {
                        ConsultationEvents ce = new ConsultationEvents();
                        ce.setDocumentClient(document.replaceAll("[\".\\-\\s]", "").trim());
                        ce.setCsvId(executor);
                        ce.setInsertionDate(now);
                        ce.setValueResult(null); 
                        ce.setUser(user.getDocument());
                        return ce;
                    })
                    .collect(Collectors.toList());

            executor.setTotalRegistered(registros.size());
            executorRepository.save(executor);

            repository.saveAll(registros);

            return ResponseEntity.ok(Map.of("csvId", csvId));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @PostMapping("/executar")
    public ResponseEntity<Map<String,String>> processarCsv(@RequestBody ProcessamentoCsvRestModel request) {
        String processoId = csvProcessManager.iniciarProcessamento(
            request.getCsvId(),
            request.getUsuarios(),
            request.getEmail(),
            logSender
        );

        Optional<Executor> executor = this.executorRepository.findCurrentProcessStatusByEmail(request.getEmail());

        if (executor.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        executor.get().setProcessId(processoId);

        this.executorRepository.save(executor.get());

        return ResponseEntity.ok(Map.of("processoId",processoId));
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
    public ResponseEntity<FindCsvStatusRestModel> buscarStatusAtualProcessamentoCsv(@PathVariable("email")String email) {

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
    @DeleteMapping(value = "/zerar-resultados/{email}")
    public ResponseEntity<String> zerarResultados(@PathVariable("email") String email) {

        Optional<Executor> executorFind = executorRepository.findCurrentProcessStatusByEmail(email);

            if (executorFind.isPresent()) {
                repository.deleteByCsvId(executorFind.get().getId());
                executorRepository.deleteById(executorFind.get().getId());
            }

        return ResponseEntity.ok().body("Dados zerados com sucesso");
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
        .map(e -> new String[]{
                e.getDocumentClient(),
                e.getValueResult()
        })
        .toList();

        String[] headers = {"Documento", "Resultado"};


        try {
            ByteArrayInputStream stream  = this.fileExportGenerationUseCase.execute(data, headers);
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
