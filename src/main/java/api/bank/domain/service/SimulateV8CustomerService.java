package api.bank.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.exception.SimulateV8CustomerException;
import api.bank.app.restmodel.BalancePeriods;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.SimulateV8CustomerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SimulateV8CustomerService implements SimulateV8CustomerUseCase {

    private final  V8BankConfigDataProvider v8BankConfigDataProvider;

    @Override
    public Double execute(RestTemplate session, String acessToken, List<BalancePeriods> listBalance, String customerCPF, String balanceId) {
        
        Double availableAmount = 0.0;

        String urlSimulate = this.v8BankConfigDataProvider.getAudience().concat("/fgts/simulations");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + acessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("documentNumber", customerCPF);
            requestBody.put("isInsured", false);
            requestBody.put("simulationFeesId", "cb563029-ba93-4b53-8d53-4ac145087212");
            requestBody.put("targetAmount", 0);
            requestBody.put("provider", "qi");
            requestBody.put("desiredInstallments", listBalance);
            requestBody.put("balanceId", balanceId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = session.postForEntity(urlSimulate, requestEntity, String.class);
            String availableBalance = new ObjectMapper()
                .readTree(response.getBody())
                .path("availableBalance")
                .asText();

            if(response.getBody() != null) {
                availableAmount = Double.parseDouble(availableBalance); 
            }

        } catch (ResourceAccessException e) {
            throw new SimulateV8CustomerException("Connection error with V8Bank");
        } catch (RestClientException e) {
            throw new SimulateV8CustomerException("Error sending request to V8Bank");
        } catch (Exception e) {
            throw new SimulateV8CustomerException("Error trying to consult balance");
        }
        
        return availableAmount;

    }

}
