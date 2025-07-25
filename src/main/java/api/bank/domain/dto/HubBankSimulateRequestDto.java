package api.bank.domain.dto;

import lombok.Data;

@Data
public class HubBankSimulateRequestDto {

    private String cpfCliente;
    private String lojaId;
    private String bancarizador;
}
