package api.bank.app.entrypoints;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import api.bank.app.restmodel.EventRequestFacebook;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/facebook")
@AllArgsConstructor
public class ConversionController {

    @Qualifier("accessTokenFb")
    private final String accessTokenFb;

    @Qualifier("pixelIdFb")
    private final String pixelIdFb;

    @PostMapping("/capi")
    public ResponseEntity<?> sendPageView(@RequestBody EventRequestFacebook eventRequest) {
        try {
            String hashedEmail = hashEmail(eventRequest.getEmail());

            Map<String, Object> userData = new HashMap<>();
            userData.put("em", Collections.singletonList(hashedEmail));

            Map<String, Object> event = new HashMap<>();
            event.put("event_name", "PageView");
            event.put("event_time", Instant.now().getEpochSecond());
            event.put("event_source_url", eventRequest.getUrl());
            event.put("action_source", "website");
            event.put("user_data", userData);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", Collections.singletonList(event));

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://graph.facebook.com/v18.0/" + pixelIdFb + "/events?access_token=" + accessTokenFb;
            restTemplate.postForObject(url, requestBody, String.class);

            return ResponseEntity.ok("Enviado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    private String hashEmail(String email) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}
