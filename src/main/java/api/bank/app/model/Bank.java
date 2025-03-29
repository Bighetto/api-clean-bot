package api.bank.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bank_tb")
@Getter
@Setter
public class Bank {

    @Id
    @Column(name = "id")
    private String id; 

    @Column(name = "name", nullable = false)
    private String name;
}
