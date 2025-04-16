package api.bank.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
