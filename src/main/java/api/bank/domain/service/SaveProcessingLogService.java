package api.bank.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.model.CsvExecutionHistory;
import api.bank.domain.dataprovider.CsvExecutionHistoryDataProvider;
import api.bank.domain.dto.CsvExecutionHistoryDto;
import api.bank.domain.usecase.SaveProcessingLogUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SaveProcessingLogService implements SaveProcessingLogUseCase {

    private final CsvExecutionHistoryDataProvider saveProcessingLogDataProvider;
    private final ObjectMapper objectMapper; // Injetado para serializar o DTO


    @Override
    public void execute(CsvExecutionHistoryDto dto) {
        String contentAsString = convertDtoToJsonString(dto.getContent());

        CsvExecutionHistory log = CsvExecutionHistory.builder()
                .content(contentAsString)
                .userEmail(dto.getUserEmail())
                .operation(dto.getOperationType())
                .dateTime(LocalDateTime.now())
                .build();

        saveProcessingLogDataProvider.save(log);
    }

    private String convertDtoToJsonString(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Falha ao serializar o corpo da requisição\", \"className\":\"" + dto.getClass().getName() + "\"}";
        }
    }

}
