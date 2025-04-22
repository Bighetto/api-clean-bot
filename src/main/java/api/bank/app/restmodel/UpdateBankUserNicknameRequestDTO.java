package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBankUserNicknameRequestDTO {

    private String bankUserId;
    private String newNickname;
}
