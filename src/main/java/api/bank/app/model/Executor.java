package api.bank.app.model;

import api.bank.app.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "query_executor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Executor {

    @Id
    private String id;

    @Column(name = "user_login", nullable = false)
    private String userLogin;

    @Column(name = "process_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus processStatus;

    @Column(name = "total_registered", nullable = true)
    private Integer totalRegistered;
}
