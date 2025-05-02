package api.bank.domain.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.bank.app.exception.GetUserBankV8TokenException;
import api.bank.app.restmodel.V8BankRequestDTO;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;
import api.bank.domain.usecase.GetUserBankV8TokenUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GetUserBankV8TokenService implements GetUserBankV8TokenUseCase {

    private final V8BankConfigDataProvider v8BankConfigDataProvider;
    private final CreateRestTemplateSessionUseCase cRestTemplateSessionUseCase;

    @Override
    public String execute(String login, String password) {
        
        String token = null;

        try {
            V8BankRequestDTO v8BankRequestDTO = new V8BankRequestDTO();
            v8BankRequestDTO.setUsername(login);
            v8BankRequestDTO.setPassword(password);
            v8BankRequestDTO.setAudience(this.v8BankConfigDataProvider.getAudience());
            v8BankRequestDTO.setClientId(this.v8BankConfigDataProvider.getClientId());
            v8BankRequestDTO.setGrantType(this.v8BankConfigDataProvider.getGrantType());
            v8BankRequestDTO.setScope(this.v8BankConfigDataProvider.getScope());

            RestTemplate restTemplate = this.cRestTemplateSessionUseCase.execute();

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", v8BankRequestDTO.getGrantType());
            formData.add("username", v8BankRequestDTO.getUsername());
            formData.add("password", v8BankRequestDTO.getPassword());
            formData.add("audience", v8BankRequestDTO.getAudience());
            formData.add("scope", v8BankRequestDTO.getScope());
            formData.add("client_id", v8BankRequestDTO.getClientId());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                this.v8BankConfigDataProvider.getV8BankURL(),
                request,
                String.class
            );

            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            token = accessToken;
                        
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new GetUserBankV8TokenException("Invalid User");
        } catch (ResourceAccessException e) {
            throw new GetUserBankV8TokenException("Connection error with V8Bank");
        } catch (RestClientException e) {
            throw new GetUserBankV8TokenException("Error sending request to V8Bank");
        } catch (JsonProcessingException e) {
            throw new GetUserBankV8TokenException("Error processing the response from V8Bank");
        }
        
        return token;
    }

}
