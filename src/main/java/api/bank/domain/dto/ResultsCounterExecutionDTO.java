package api.bank.domain.dto;

import lombok.Data;

@Data
public class ResultsCounterExecutionDTO {
    private Integer totalConsultas;
    private Integer naoAutorizado;
    private Integer erro;
    private Integer semSaldo;
    private Integer comSaldo;
}
