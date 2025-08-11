package api.bank.app.restmodel;

import java.util.List;

import lombok.Data;

@Data
public class ProcessamentoCsvRestModel {
    private String csvId;
    private String email;
    private String bankName;
    private List<String> usuarios;
}

