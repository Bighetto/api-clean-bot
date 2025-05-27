package api.security.auth.app.entrypoints;

import java.util.Map;

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

    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            String status = (String) payload.get("status");

            if (!"authorized".equalsIgnoreCase(status)) {
                return ResponseEntity.ok("Pagamento não autorizado. Usuário não será criado.");
            }

            String evento = ((Map<String, Object>) payload.getOrDefault("url_params", Map.of()))
                    .getOrDefault("query_params", Map.of())
                    .toString();

            System.out.println("Evento recebido: " + evento);

            Map<String, Object> customer = (Map<String, Object>) payload.get("customer");

            String document = customer.get("cpf") != null
                    ? (String) customer.get("cpf")
                    : (String) customer.get("cnpj");

            String name = (String) customer.get("name");
            String email = (String) customer.get("email");
            String phoneNumber = (String) customer.get("phone");

            Map<String, Object> item = (Map<String, Object>) payload.get("item");
            String offerCode = (String) item.get("offer_code");

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
