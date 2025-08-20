package api.bank.domain.service;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.exception.HubBankResponseErrorTypeException;
import api.bank.app.exception.SimulateHubBankCustomerException;
import api.bank.domain.dataprovider.HubBankConfigDataProvider;
import api.bank.domain.dto.HubBankResponseErrorType;
import api.bank.domain.dto.HubBankSimulateRequestDto;
import api.bank.domain.usecase.SimulateHubBankCustomerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SimulateHubBankCustomerService implements SimulateHubBankCustomerUseCase {

    private final HubBankConfigDataProvider hubBankConfigDataProvider;
    private final Integer maxRetries = 3;

    private static final Set<HubBankResponseErrorType> RETRYABLE_ERRORS = EnumSet.of(
        HubBankResponseErrorType.TOO_MANY_REQUESTS,
        HubBankResponseErrorType.SIMULATION_IN_PROGRESS,
        HubBankResponseErrorType.REQUEST_TIMEOUT,
        HubBankResponseErrorType.QUERY_LIMIT_EXCEEDED,
        HubBankResponseErrorType.FIDUCIARY_OPERATION_IN_PROGRESS
    );

    @Override
    public String execute(RestTemplate session, String acessToken, String customerCPF) {
        Integer retries = 0;
        final String HUB_BANK_SIMULATE_URL = this.hubBankConfigDataProvider.getHubBankURLSimulate();
        
        while (retries < maxRetries) {
            try {
                HttpEntity<HubBankSimulateRequestDto> request = createRequest(acessToken, customerCPF);
                
                ResponseEntity<String> responseEntity = session.postForEntity(
                    HUB_BANK_SIMULATE_URL, 
                    request, 
                    String.class
                );

                return String.valueOf(parseValue(responseEntity));

            } catch (HttpClientErrorException e) {
                
                if(e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                    throw new SimulateHubBankCustomerException("Usuario não autenticado");
                }
                
                try {
                    HubBankResponseErrorType errorType = parseErrorResponse(e);

                    if (RETRYABLE_ERRORS.contains(errorType)) {
                        retries++;
                        sleepAndRetry();
                        continue; 
                    }
                    return mapErrorTypeToMessage(errorType);
                } catch (JsonProcessingException jsonEx) {
                    throw new SimulateHubBankCustomerException("Falha ao processar a resposta de erro do banco Hub.");
                }
            } catch (ResourceAccessException e) {
                throw new SimulateHubBankCustomerException("Erro de conexão com o banco Hub");
            } catch (RestClientException e) {
                throw new SimulateHubBankCustomerException("Erro enviando requisição para o banco Hub");
            } catch (JsonProcessingException e) {
                throw new SimulateHubBankCustomerException("Falha ao processar o JSON de uma resposta de sucesso do banco Hub.");
            }
        } 
        
        return "ERRO NA REQUISIÇÃO";
    }

    private HttpEntity<HubBankSimulateRequestDto> createRequest(String acessToken, String customerCpf) {
        HubBankSimulateRequestDto dto = createHubBankSimulateRequestDto(customerCpf);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + acessToken);
        
        HttpEntity<HubBankSimulateRequestDto> request = new HttpEntity<>(dto, headers);

        return request;
    }

    private HubBankSimulateRequestDto createHubBankSimulateRequestDto(String customerCPF) {
        HubBankSimulateRequestDto dto = new HubBankSimulateRequestDto();
        dto.setCpfCliente(customerCPF);
        dto.setLojaId(hubBankConfigDataProvider.getLojaId());
        dto.setBancarizador("BMP");

        return dto;
    }

    private Double parseValue(ResponseEntity<String> responseEntity) throws JsonMappingException, JsonProcessingException {
        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);

        JsonNode valueNode = rootNode.path("value").path(0).path("valorCliente");

        if (valueNode.isMissingNode()) {
             throw new SimulateHubBankCustomerException("Campo 'valorCliente' não encontrado na estrutura JSON esperada.");
        }
        return valueNode.asDouble();
    }

    private HubBankResponseErrorType parseErrorResponse(HttpClientErrorException exception) throws JsonMappingException, JsonProcessingException {
        String responseBody = exception.getResponseBodyAsString();
        JsonNode rootNode = new ObjectMapper().readTree(responseBody);
        String errorMessage = rootNode.path("errors").get(0).asText();
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
            try {
                return HubBankResponseErrorType.fromMessage(errorMessage);
            } catch (HubBankResponseErrorTypeException e) {
                throw new SimulateHubBankCustomerException("Tipo de erro de negócio não mapeado pela aplicação: " + errorMessage);
            }
        }

        throw new SimulateHubBankCustomerException("Não foi possível extrair a mensagem de erro da resposta da API.");
    }

    private String mapErrorTypeToMessage(HubBankResponseErrorType errorType) {
        switch (errorType) {
            case WORKER_NOT_IN_BIRTHDAY_WITHDRAWAL:
            case INSUFFICIENT_BALANCE:
            case NO_AVAILABLE_BALANCE:
            case COSTS_HIGHER_THAN_FINANCED_AMOUNT:
                return "SEM SALDO";

            case FIDUCIARY_INSTITUTION:
            case BANKS_NOT_AUTHORIZED:
            case OPERATION_TEMPORARILY_BLOCKED:
            case WORKER_HAS_NO_FGTS_ACCOUNT: 
                return "NÃO AUTORIZADO";
                
            default:
                return "NÃO AUTORIZADO";
        }
    }

    private void sleepAndRetry() {   
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulateHubBankCustomerException("A thread foi interrompida durante o tempo de espera para retentativa.");
        }
    }
    
}
