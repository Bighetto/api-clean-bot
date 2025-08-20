package api.bank.domain.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.exception.GetUserBankHubTokenException;
import api.bank.domain.dataprovider.HubBankConfigDataProvider;
import api.bank.domain.dto.HubBankGetTokenRequestDto;
import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;
import api.bank.domain.usecase.GetUserBankHubTokenUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GetUserBankHubTokenService implements GetUserBankHubTokenUseCase {
    
    private final HubBankConfigDataProvider hubBankConfigDataProvider;
    private final CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;
    
    @Override
    public String execute(String login, String password) {

        try {
            HubBankGetTokenRequestDto dto = createHubBankGetTokenRequestDto(login, password);
            ResponseEntity<String> response = sendRequest(dto);
            
            return parseToken(response);    
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new GetUserBankHubTokenException("Erro de comunicação com o servidor do Hub Bank: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new GetUserBankHubTokenException("Erro de conexão com o Hub Bank.");
        } catch (RestClientException e) {
            throw new GetUserBankHubTokenException("Erro na chamada para o Hub Bank.");
        } catch (JsonProcessingException e) {
            throw new GetUserBankHubTokenException("Erro ao processar a resposta (JSON inválido) do Hub Bank.");
        }
    }


    private HubBankGetTokenRequestDto createHubBankGetTokenRequestDto(String login, String password) {
        HubBankGetTokenRequestDto dto = new HubBankGetTokenRequestDto();
        dto.setUsername(login);
        dto.setPassword(password);
        dto.setGrantTypes(this.hubBankConfigDataProvider.getGrantType());

        return dto;
    }

    private ResponseEntity<String> sendRequest(HubBankGetTokenRequestDto dto) {
        RestTemplate restTemplate = this.createRestTemplateSessionUseCase.execute();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HubBankGetTokenRequestDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            this.hubBankConfigDataProvider.getHubBankURLGetToken(),
            request,
            String.class
        );

        return response;
    }

    private String parseToken(ResponseEntity<String> responseEntity) throws JsonMappingException, JsonProcessingException {
        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);

        JsonNode tokenNode = rootNode.path("value").path("token");

        if (!tokenNode.isMissingNode()) {
            String accessToken = tokenNode.path("accessToken").asText();

            if (accessToken != null && !accessToken.isEmpty()) {
                return accessToken;
            } else {
                throw new GetUserBankHubTokenException("Resposta de sucesso do Hub Bank, mas o token está vazio.");
            }
        } else {
            throw new GetUserBankHubTokenException("Usuario invalido");
        }
    }   
}
