package api.bank.domain.service;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.app.restmodel.V8BankRequestDTO;
import api.bank.domain.dataprovider.V8BankConfigDataProvider;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ValidateUserBankV8Service implements ValidateUserBankV8UseCase {

    private final V8BankConfigDataProvider v8BankDataProvider;

    @Override
    public void execute(UploadBankUserRequestRestModel requestRestModel) throws IOException {

        try {
            V8BankRequestDTO v8BankRequestDTO = new V8BankRequestDTO();
            v8BankRequestDTO.setUsername(requestRestModel.getLogin());
            v8BankRequestDTO.setPassword(requestRestModel.getPassword());
            v8BankRequestDTO.setAudience(this.v8BankDataProvider.getAudience());
            v8BankRequestDTO.setClientId(this.v8BankDataProvider.getClientId());
            v8BankRequestDTO.setGrantType(this.v8BankDataProvider.getGrantType());
            v8BankRequestDTO.setScope(this.v8BankDataProvider.getScope());

            RestTemplate restTemplate = new RestTemplate();

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
                    this.v8BankDataProvider.getV8BankURL(),
                    request,
                    String.class
                );
            
        } catch (Exception e) {
            
        }
    }

}
