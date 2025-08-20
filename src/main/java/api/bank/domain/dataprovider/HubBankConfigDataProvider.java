package api.bank.domain.dataprovider;

public interface HubBankConfigDataProvider {

    String getHubBankURLGetToken();

    String getHubBankURLSimulate();

    String getLojaId();

    String getGrantType();
}
