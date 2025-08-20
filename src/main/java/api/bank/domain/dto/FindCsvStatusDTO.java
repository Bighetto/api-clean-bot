package api.bank.domain.dto;

import lombok.Data;

@Data
public class FindCsvStatusDTO {
    private String idCsv;
    private String status;
    private Integer quantidadeDocumentos;
    private ResultsCounterExecutionDTO results;
}