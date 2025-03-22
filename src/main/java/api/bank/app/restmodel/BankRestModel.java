package api.bank.app.restmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankRestModel {

    private String name;
    private List<UserBankRestModel> usersBank;
    
}
