package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.domain.dataprovider.HubBankConfigDataProvider;
import api.config.HubBankConfig;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HubBankConfigProvider implements HubBankConfigDataProvider {

    private final HubBankConfig hubBankConfig;

    @Override
    public String getHubBankURLGetToken() {
        return this.hubBankConfig.getHubBankURLGetToken();
    }

    @Override
    public String getHubBankURLSimulate() {
        return this.hubBankConfig.getHubBankURLSimulate();
    }

    @Override
    public String getLojaId() {
        return this.hubBankConfig.getLojaId();
    }

    @Override
    public String getGrantType() {
        return this.hubBankConfig.getGrantType();
    }

}
