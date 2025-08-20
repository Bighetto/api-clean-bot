package api.bank.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.exception.UploadCsvException;
import api.bank.app.model.Bank;
import api.bank.app.model.ConsultationEvents;
import api.bank.app.model.Executor;
import api.bank.app.restmodel.UploadCsvDTO;
import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.usecase.UploadCsvUseCase;
import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class UploadCsvService implements UploadCsvUseCase {
    
    private final ExecutorDataProvider executorDataProvider;
    private final ConsultationEventsDataProvider consultationEventsDataProvider;
    private static final int BATCH_SIZE = 2000;

    @Override
    public String execute(UploadCsvDTO dto) {

        String csvId = UUID.randomUUID().toString();

        try {
            Executor executor = createInitialExecutor(dto, csvId);

            List<ConsultationEvents> registros = parseCsvToEvents(dto.getFile(), executor);

            executor.setTotalRegistered(registros.size());

            this.executorDataProvider.save(executor);
            
            saveEventsInBatches(registros);

            return csvId;
        } catch (IOException e) {
            throw new UploadCsvException("An error occurred while reading the CSV file.");
        } catch (Exception e) {
            throw new UploadCsvException("An unexpected internal error occurred during CSV processing.");
        }    
    }

    private Executor createInitialExecutor(UploadCsvDTO dto, String csvId) {
        Bank bank = new Bank();
        bank.setId(dto.getBankId());

        Executor executor = new Executor();
        executor.setId(csvId);
        executor.setUserLogin(dto.getEmail());
        executor.setProcessStatus(ProcessStatus.PENDENTE);
        executor.setBank(bank);
        return executor;
    }

    private List<ConsultationEvents> parseCsvToEvents(MultipartFile file, Executor executor) throws IOException {
        final LocalDateTime now = LocalDateTime.now();
        final Bank bank = executor.getBank();
        final String userDocument = executor.getUserLogin(); 

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(document -> {
                        ConsultationEvents ce = new ConsultationEvents();
                        ce.setDocumentClient(document.replaceAll("[.,;\\s-]", "").trim());
                        ce.setCsvId(executor);
                        ce.setInsertionDate(now);
                        ce.setValueResult(null);
                        ce.setUser(userDocument);
                        ce.setBank(bank);
                        return ce;
                    })
                    .collect(Collectors.toList());
        }
    }

    private void saveEventsInBatches(List<ConsultationEvents> events) {
        if (events.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < events.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, events.size());
            List<ConsultationEvents> batch = events.subList(i, end);
            this.consultationEventsDataProvider.saveAll(batch);
        }
    }

}
