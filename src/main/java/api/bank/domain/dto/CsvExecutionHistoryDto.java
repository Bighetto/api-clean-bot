package api.bank.domain.dto;

import api.bank.app.restmodel.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvExecutionHistoryDto {
    Object content;
    String userEmail;
    OperationType operationType;
}
