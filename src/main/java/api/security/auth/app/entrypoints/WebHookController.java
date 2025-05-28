package api.security.auth.app.entrypoints;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.security.auth.app.restmodel.UserRestModel;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/webhook/")
@AllArgsConstructor
public class WebHookController {

    private final UserResource userResource;

    @Qualifier("webhookToken")
    private final String webhookToken;


    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {

            String receivedToken = (String) payload.get("token");

            if (!webhookToken.equals(receivedToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido.");
            }

            String status = (String) payload.get("status");

            if (!"authorized".equalsIgnoreCase(status)) {
                return ResponseEntity.ok("Pagamento não autorizado. Usuário não será criado.");
            }

            Map<String, Object> urlParams = (Map<String, Object>) payload.getOrDefault("url_params", Map.of());
            Map<String, Object> queryParams = (Map<String, Object>) urlParams.getOrDefault("query_params", Map.of());
            String code = (String) queryParams.get("code");

            System.out.println("Evento recebido: " + code);

            Map<String, Object> customer = (Map<String, Object>) payload.get("customer");

            String document = customer.get("cpf") != null
                    ? (String) customer.get("cpf")
                    : (String) customer.get("cnpj");

            String name = (String) customer.get("name");
            String email = (String) customer.get("email");
            Map<String, Object> phone = (Map<String, Object>) customer.get("phone");
            String ddi = (String) phone.get("ddi");
            String ddd = (String) phone.get("ddd");
            String number = (String) phone.get("number");
            String phoneNumber = ddi + ddd + number;

            Map<String, Object> item = (Map<String, Object>) payload.get("item");
            String offerCode = (String) item.get("offer_id");

            UserRestModel user = new UserRestModel(
                    document,
                    name,
                    email,
                    phoneNumber,
                    offerCode
            );

            return userResource.registerUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro no processamento do webhook: " + e.getMessage());
        }
    }

}
