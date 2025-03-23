package api.bank.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanEntity {

    private String id;
    private String name;
    private Double price;
    private Integer limitUsers;
    private Integer limitBanks;
    private Integer limitRequests;
}
