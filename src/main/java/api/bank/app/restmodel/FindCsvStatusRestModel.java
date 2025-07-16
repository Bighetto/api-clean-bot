package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindCsvStatusRestModel {
    
    private String idCsv;
    private String status;
    private Integer quantidadeDocumentos;
    private ResultsCounterExecutionRestModel results;
}
