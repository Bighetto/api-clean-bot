package api.bank.domain.dto;

import lombok.Data;

@Data
public class HubBankGetTokenRequestDto {

    private String username;
    private String password;
    private String grantTypes;
}
