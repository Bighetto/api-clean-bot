package api.security.auth.app.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens_tb")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Token {

    @Id
    private String userEmail;
    private String token;
    private LocalDateTime expirationDateTime;

}
