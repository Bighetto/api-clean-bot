package api.bank.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plans_tb")
@Getter
@Setter
public class Plan {
    
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer limitUsers;

    @Column(nullable = false)
    private Integer limitBanks;

    @Column(nullable = false)
    private Integer limitRequests;
}

