package api.bank.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.exception.ConsultV8CustomerBalanceException;
import api.bank.app.exception.GetUserBankV8TokenException;
import api.bank.app.restmodel.BalancePeriods;
import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
import api.bank.app.restmodel.V8ResponseErrorType;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.ConsultV8CustomerBalanceUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConsultV8CustomerBalanceService implements ConsultV8CustomerBalanceUseCase {
    
    private final V8BankConfigDataProvider v8BankConfigDataProvider;

    @Override
    public ConsultV8CustomerBalanceResponse execute(RestTemplate session, String acessToken, String customerCPF) {
        
        String urlConsultBalance = this.v8BankConfigDataProvider.getAudience().concat("/fgts/balance");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + acessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("documentNumber", customerCPF);
            requestBody.put("provider", "bms");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        Integer retries = 0;
        Integer maxRetries = 3;

        ConsultV8CustomerBalanceResponse dto = new ConsultV8CustomerBalanceResponse();
        
        while (retries < maxRetries) {

            ResponseEntity<String> response = null;
            String balance = null;

            try {
                response = session.postForEntity(urlConsultBalance, requestEntity, String.class);
                balance = response.getBody();

                if(response.getBody().equals("null")) {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlConsultBalance)
                    .queryParam("search", customerCPF)
                    .queryParam("status", "success")
                    .queryParam("page", 1)
                    .queryParam("limit", 1);

                    HttpEntity<Void> getRequest = new HttpEntity<>(headers);
                    response = session.exchange(builder.toUriString(), HttpMethod.GET, getRequest, String.class);
                    balance = response.getBody();  
                } 

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(balance);
                JsonNode dataNode = rootNode.has("data") ? rootNode.get("data") : mapper.createArrayNode();

                JsonNode firstDataElement = dataNode.get(0);

                String balanceId = firstDataElement.get("id").asText();
                JsonNode balancePeriods = firstDataElement.has("periods") ? firstDataElement.get("periods") : mapper.createArrayNode();

                List<Object> newBalancePeriods = new ArrayList<>();

                if (balancePeriods != null && balancePeriods.isArray()) {
                    for (JsonNode item : balancePeriods) {
                        if (item.has("amount") && item.has("dueDate")) {
                            double totalAmount = item.get("amount").asDouble();
                            String dueDate = item.get("dueDate").asText();

                            BalancePeriods newItem = new BalancePeriods(totalAmount, dueDate);
                            newBalancePeriods.add(newItem);
                        }
                    }
                }

                dto.setBalancePeriods(newBalancePeriods);
                dto.setBalanceId(balanceId);
                dto.setBooleanValue(true);

            } catch (HttpClientErrorException  e) {

                switch (e.getStatusCode().value()) {
                    case 400:
                        throw new ConsultV8CustomerBalanceException("Invalid document");

                    case 401:
                        throw new ConsultV8CustomerBalanceException("User unauthorized");

                    case 429:
                        sleepAndRetry();
                        retries += 1;
                        
                    default:
                        throw new ConsultV8CustomerBalanceException("Unexpected http response");
                }
                
            } catch (HttpServerErrorException e) {

                try {
                    String errorMessage = new ObjectMapper()
                    .readTree(e.getResponseBodyAsString())
                    .path("error")
                    .asText();

                    V8ResponseErrorType errorType = V8ResponseErrorType.fromMessage(errorMessage);

                    if (errorType != null) {

                        switch (errorType) {
                            case VALUES_MUST_HAVE_AT_LEAST_ONE_VALUE:  
                            case INSUFFICIENT_BALANCE:
                            case COSTS_EXCEED_FINANCED_AMOUNT:
                            case WORKER_NOT_BIRTHYDAY:
                                setNoBalance(dto);
                                return dto;

                            case CANNOT_READ_PROPERTY_MAP:
                            case FIDUCIARY_INSTITUTION:
                            case CLIENT_NOT_AUTHORIZED:
                            case EMPTY_RESPONSE:
                                setNotAuthorized(dto);
                                return dto;

                            case FAILED_TO_FETCH_AVAILABLE_BALANCE:
                            case SERVICE_UNAVAILABLE:
                            case RATE_LIMIT_EXCEEDED:
                            case TOO_MANY_REQUESTS:
                                sleepAndRetry();
                                retries += 1;

                            default:
                                setNotAuthorized(dto);
                                return dto;
                        }
                    } else {
                        throw new ConsultV8CustomerBalanceException("Error processing server response from V8Bank");
                    }

                } catch (JsonMappingException e1) {
                    throw new ConsultV8CustomerBalanceException("Error mapping Json");
                } catch (JsonProcessingException e1) {
                    throw new ConsultV8CustomerBalanceException("Error processing Json");
                }
    
            } catch (ResourceAccessException e) {
                throw new ConsultV8CustomerBalanceException("Connection error with V8Bank");
            } catch (RestClientException e) {
                throw new GetUserBankV8TokenException("Error sending request to V8Bank");
            } catch (JsonProcessingException e) {
                throw new ConsultV8CustomerBalanceException("Error processing response from Bank V8");
            } 
        }

        return dto;
    }

    private void setNoBalance(ConsultV8CustomerBalanceResponse dto) {
        dto.setBalancePeriods("SEM SALDO");
        dto.setBalanceId(null);
        dto.setBooleanValue(true);
    }

    private void setNotAuthorized(ConsultV8CustomerBalanceResponse dto) {
        dto.setBalancePeriods("NÃO AUTORIZADO");
        dto.setBalanceId(null);
        dto.setBooleanValue(false);
    }

    private void sleepAndRetry() {
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

}
