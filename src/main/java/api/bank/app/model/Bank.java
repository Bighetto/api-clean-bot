package api.bank.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Id
    @Column(name = "id")
    private String id; 

    @Column(name = "name", nullable = false)
    private String name;
}
