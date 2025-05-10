package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultsCounterExecutionRestModel {
    
    private Integer total_consultas;
    private Integer nao_autorizado;
    private Integer erro;
    private Integer sem_saldo;
    private Integer com_saldo;
}
