package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalancePeriods {

    private Double totalAmount;
    private String dueDate;
}
