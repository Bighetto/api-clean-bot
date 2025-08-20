package api.bank.app.model;

import java.time.LocalDateTime;

import api.bank.app.restmodel.OperationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "processing_history_tb")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CsvExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Lob
    private String content;
    private String userEmail;
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private OperationType operation;
    
}
