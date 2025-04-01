package api.security.auth.app.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "document", nullable = false)
    private UserLogin user;

    private String token;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isExpired;
    
    public boolean isExpired() {
        return createdAt.plusMinutes(15).isBefore(LocalDateTime.now());
    }

    public Token(UserLogin user, String token, Boolean isExpired) {
        this.user = user;
        this.token = token;
        this.isExpired = isExpired;
    }
}
