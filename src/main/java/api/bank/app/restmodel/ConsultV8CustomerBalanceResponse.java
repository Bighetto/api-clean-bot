package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultV8CustomerBalanceResponse {

    private Object balancePeriods;
    private String balanceId;
    private Boolean validCustomer;
}
