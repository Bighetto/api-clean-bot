package api.bank.domain.dataprovider;

public interface V8BankConfigDataProvider {

    String getV8BankURL();

    String getAudience();

    String getClientId();

    String getGrantType();

    String getScope();
}
