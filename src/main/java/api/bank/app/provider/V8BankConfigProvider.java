package api.bank.app.provider;

import org.springframework.stereotype.Component;

import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.config.V8BankConfig;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class V8BankConfigProvider implements V8BankConfigDataProvider{
    
    private final V8BankConfig v8BankConfig;

    @Override
    public String getV8BankURL() {
        return this.v8BankConfig.getV8BankURL();
    }

    @Override
    public String getAudience() {
        return v8BankConfig.getAudience();
    }

    @Override
    public String getClientId() {
        return v8BankConfig.getClientId();
    }

    @Override
    public String getGrantType() {
        return v8BankConfig.getGrantType();
    }

    @Override
    public String getScope() {
        return v8BankConfig.getScope();
    }

}
