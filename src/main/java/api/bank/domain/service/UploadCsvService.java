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
import api.bank.app.exception.UploadCsvExecption;
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

    //Result Value tava salvo como null mas anotado como not null no banco.
    private static final String DEFAULT_RESULT_VALUE = "1";


    @Override
    public String execute(UploadCsvDTO dto) {

        String userDocument = dto.getUserDocument();
        MultipartFile file = dto.getFile();
        if (file == null || file.isEmpty()) {
            throw new UploadCsvExecption("The CSV file cannot be null or empty.");
        }

        String csvId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        Bank bank = new Bank();
        bank.setId(dto.getBankId());

        Executor executor = new Executor();
        executor.setId(csvId);
        executor.setUserLogin(dto.getEmail());
        executor.setProcessStatus(ProcessStatus.PENDENTE);
        executor.setBank(bank);

        try {
            List<ConsultationEvents> registros = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(document -> {
                        ConsultationEvents ce = new ConsultationEvents();
                        ce.setDocumentClient(document.replaceAll("[.,;\\s-]", "").trim());
                        ce.setCsvId(executor);
                        ce.setInsertionDate(now);
                        ce.setValueResult(DEFAULT_RESULT_VALUE);
                        ce.setUser(userDocument);
                        ce.setBank(bank);
                        return ce;
                    })
                    .collect(Collectors.toList());

            executor.setTotalRegistered(registros.size());

            this.executorDataProvider.save(executor);

            final int BATCH_SIZE = 2000;
            for (int i = 0; i < registros.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, registros.size());
                List<ConsultationEvents> batch = registros.subList(i, end);
                this.consultationEventsDataProvider.saveAll(batch);
            }

            return csvId;
        } catch (IOException e) {
            throw new UploadCsvExecption("An error occurred while reading the CSV file.");
        } catch (Exception e) {
            throw new UploadCsvExecption("An unexpected internal error occurred during CSV processing.");
        }    
    }

}
