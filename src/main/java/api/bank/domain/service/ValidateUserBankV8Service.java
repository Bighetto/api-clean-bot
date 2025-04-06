package api.bank.domain.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.UploadBankUserRequestRestModel;
import api.bank.domain.usecase.ValidateUserBankV8UseCase;
import api.config.V8BankEnviroment;

@Component
public class ValidateUserBankV8Service implements ValidateUserBankV8UseCase {

    @Value("${api.v8.bank.url}")
    private String urlGetToken;

    private final V8BankEnviroment v8BankEnviroment;

    public ValidateUserBankV8Service(@Value("${api.v8.bank.url}") String urlGetToken, V8BankEnviroment v8BankEnviroment) {
        this.urlGetToken = urlGetToken;
        this.v8BankEnviroment = v8BankEnviroment;
    }

    @Override
    public void execute(UploadBankUserRequestRestModel requestRestModel) throws IOException {

        try {
            v8BankEnviroment.setUsername(requestRestModel.getLogin());
            v8BankEnviroment.setPassword(requestRestModel.getPassword());

            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", v8BankEnviroment.getGrantType());
            formData.add("username", v8BankEnviroment.getUsername());
            formData.add("password", v8BankEnviroment.getPassword());
            formData.add("audience", v8BankEnviroment.getAudience());
            formData.add("scope", v8BankEnviroment.getScope());
            formData.add("client_id", v8BankEnviroment.getClientId());

            System.out.println("grant_type: " + v8BankEnviroment.getGrantType());
            System.out.println("audience: " + v8BankEnviroment.getAudience());
            System.out.println("scope: " + v8BankEnviroment.getScope());
            System.out.println("client_id: " + v8BankEnviroment.getClientId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    urlGetToken,
                    request,
                    String.class
                );

            System.out.println("Body: " + response.getBody());

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
