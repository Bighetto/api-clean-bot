package api.security.auth.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import api.bank.app.model.BankUser;
import api.bank.app.model.Plan;
import api.security.auth.domain.enums.UserStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "user_login")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin implements UserDetails {

    private String document;

    private String name;
    
    @Id
    @Column(unique = true)
    private String email;
    
    private String phoneNumber;
    private String tipo;
    private String password;
    

    @Column(updatable = false)
    private LocalDateTime registerDate = LocalDateTime.now();

    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    private UserStatusEnum status = UserStatusEnum.ATIVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BankUser> userBanks = new ArrayList<>();

    public UserLogin(
    String document,
    String name,
    String email,
    String phoneNumber,
    String tipo,
    String password,
    LocalDateTime registerDate,
    Plan plan,
    List<BankUser> userBanks,
    UserStatusEnum status
) {
    this.document = document;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.tipo = tipo;
    this.password = password;
    this.registerDate = registerDate;
    this.userBanks = (userBanks != null) ? userBanks : new ArrayList<>();
    this.status = (status != null) ? status : UserStatusEnum.ATIVO; 

    setPlan(plan);
}

    public void setPlan(Plan plan) {
        this.plan = plan;
        this.expirationDate = calculateExpirationDate(plan);
        this.status = UserStatusEnum.ATIVO;
    }

    private LocalDateTime calculateExpirationDate(Plan plan) {
        if (plan == null) {
            return null;
        }
        if (plan.getName().toLowerCase().contains("mensal")) {
            return registerDate.plusDays(30);
        } else if (plan.getName().toLowerCase().contains("anual")) {
            return registerDate.plusDays(365);
        }
        return null;
    }

    public void updateStatus() {
        if (expirationDate != null && expirationDate.isBefore(LocalDateTime.now())) {
            this.status = UserStatusEnum.INATIVO;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(tipo));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status == UserStatusEnum.ATIVO;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == UserStatusEnum.ATIVO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status == UserStatusEnum.ATIVO;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatusEnum.ATIVO;
    }
}