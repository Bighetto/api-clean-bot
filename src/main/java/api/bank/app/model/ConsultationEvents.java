package api.bank.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Table(name = "queries_tb")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "document_client")
    private String documentClient;

    @Column(name = "value_result")
    private String valueResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csv_id", referencedColumnName = "id")
    private Executor csvId;

    @Column(name = "insertion_date")
    private LocalDateTime insertionDate;

    @Column(name = "user_document")
    private String user;

}
