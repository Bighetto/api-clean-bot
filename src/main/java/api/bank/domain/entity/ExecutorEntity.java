package api.bank.domain.entity;

import api.bank.app.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutorEntity {

    private String id;
    private String userLogin;
    private ProcessStatus processStatus;
    private Integer totalRegistered;
    private String processId;
    private String bankId;
}
