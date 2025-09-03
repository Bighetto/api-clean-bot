package api.bank.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProcessamentoCsvDTO {

    private String csvId;
    private String email;
    private String bankName;
    private List<String> usuarios;
    private String nomeTabelaFiltro;
}
