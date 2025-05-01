package api.bank.app.restmodel;

import java.util.List;

public record SimulateTest(String token, List<BalancePeriods> response, String cpf, String balanceId) {

}
