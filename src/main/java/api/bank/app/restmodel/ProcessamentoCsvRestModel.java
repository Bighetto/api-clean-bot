package api.bank.app.restmodel;

import java.util.List;

import lombok.Data;

@Data
public class ProcessamentoCsvRestModel {
    private String csvId;
    private List<String> usuarios;
}

