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

    @Column(name = "csv_id")
    private String csvId;

    @Column(name = "insertion_date")
    private LocalDateTime insertionDate;
}
