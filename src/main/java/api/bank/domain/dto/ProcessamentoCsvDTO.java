package api.bank.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProcessamentoCsvDTO {

    private String csvId;
    private String email;
    private String bankId;
    private List<String> usuarios;
}
