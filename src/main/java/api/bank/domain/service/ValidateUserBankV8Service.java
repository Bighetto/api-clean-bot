package api.bank.domain.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import api.bank.app.exception.UserBankV8ValidationException;
import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.app.restmodel.V8BankRequestDTO;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ValidateUserBankV8Service implements ValidateUserBankV8UseCase {

    private final V8BankConfigDataProvider v8BankDataProvider;
    private final CreateRestTemplateSessionService createRestTemplateSessionService;

    @Override
    public void execute(UploadBankUserRequestRestModel requestRestModel) {

        try {
            V8BankRequestDTO v8BankRequestDTO = new V8BankRequestDTO();
            v8BankRequestDTO.setUsername(requestRestModel.getLogin());
            v8BankRequestDTO.setPassword(requestRestModel.getPassword());
            v8BankRequestDTO.setAudience(this.v8BankDataProvider.getAudience());
            v8BankRequestDTO.setClientId(this.v8BankDataProvider.getClientId());
            v8BankRequestDTO.setGrantType(this.v8BankDataProvider.getGrantType());
            v8BankRequestDTO.setScope(this.v8BankDataProvider.getScope());

            RestTemplate restTemplate = this.createRestTemplateSessionService.execute();

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

            restTemplate.postForEntity(
                this.v8BankDataProvider.getV8BankURL(),
                request,
                String.class
            );
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new UserBankV8ValidationException("Invalid User");
        } catch (ResourceAccessException e) {
            throw new UserBankV8ValidationException("Connection error with V8Bank");
        } catch (RestClientException e) {
            throw new UserBankV8ValidationException("Error sending request to V8Bank");
        }
    }

}
