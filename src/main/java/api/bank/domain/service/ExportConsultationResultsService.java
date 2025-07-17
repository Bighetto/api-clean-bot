package api.bank.domain.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.entity.ConsultationEventsEntity;
import api.bank.domain.entity.ExecutorEntity;
import api.bank.domain.usecase.ExportConsultationResultsUseCase;
import api.bank.domain.usecase.FileExportGenerationUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExportConsultationResultsService implements ExportConsultationResultsUseCase {
    
    private final ExecutorDataProvider executorDataProvider;
    private final ConsultationEventsDataProvider consultationEventsDataProvider;
    private final FileExportGenerationUseCase fileExportGenerationUseCase;

    @Override
    public byte[] execute(String email, String bankId) {
        ExecutorEntity executorFind = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(email, bankId);
        List<ConsultationEventsEntity> events = this.consultationEventsDataProvider.findAllByCsvId(executorFind.getId());

        List<String[]> data = events.stream()
                .map(event -> new String[] {
                        event.getDocumentClient(),
                        event.getValueResult()
                })
                .toList();

        String[] headers = { "Documento", "Resultado" };

        ByteArrayInputStream stream;

        stream = this.fileExportGenerationUseCase.execute(data, headers);
        byte[] bytes = stream.readAllBytes();
        return bytes;
    }
}
