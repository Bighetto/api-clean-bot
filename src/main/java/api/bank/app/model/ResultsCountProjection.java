package api.bank.app.model;

public interface ResultsCountProjection {

    Integer getTotal_consultas();
    Integer getNao_autorizado();
    Integer getErro();
    Integer getSem_saldo();
    Integer getCom_saldo();
    
}
