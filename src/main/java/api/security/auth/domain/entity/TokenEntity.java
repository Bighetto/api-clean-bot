package api.security.auth.domain.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class TokenEntity {

    private String userEmail;
    private String token;
    private LocalDateTime expirationDateTime;
}
