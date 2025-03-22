package api.bank.app.model;

import api.security.auth.app.model.UserLogin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bank_user")
@Getter
@Setter
public class BankUser {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "document")
    private UserLogin user;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;
}
