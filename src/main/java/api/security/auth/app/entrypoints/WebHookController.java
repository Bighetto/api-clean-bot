package api.security.auth.app.entrypoints;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.security.auth.app.restmodel.UserRestModel;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.usecase.ActivateUserUseCase;
import api.security.auth.domain.usecase.SearchUserByEmailUseCase;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/webhook/")
@AllArgsConstructor
public class WebHookController {

    private final UserResource userResource;

    @Qualifier("webhookToken")
    private final String webhookToken;

    private final SearchUserByEmailUseCase searchUserByEmailUseCase;
    private final ActivateUserUseCase activateUserUseCase;


    @PostMapping
    public ResponseEntity<String> receiveAuthorized(@RequestBody Map<String, Object> payload) {
        try {

            String receivedToken = (String) payload.get("token");

            if (!webhookToken.equals(receivedToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido.");
            }

            String status = (String) payload.get("status");

            if (!"authorized".equalsIgnoreCase(status) && !"uncanceled".equalsIgnoreCase(status)  ) {
                return ResponseEntity.ok("Pagamento não autorizado. Usuário não será criado.");
            }

            Map<String, Object> urlParams = (Map<String, Object>) payload.getOrDefault("url_params", Map.of());
            Map<String, Object> queryParams = (Map<String, Object>) urlParams.getOrDefault("query_params", Map.of());
           

            Map<String, Object> customer = (Map<String, Object>) payload.get("customer");

            String document = customer.get("cpf") != null
                    ? (String) customer.get("cpf")
                    : (String) customer.get("cnpj");

            String name = (String) customer.get("name");
            String email = (String) customer.get("email");
            Map<String, Object> phone = (Map<String, Object>) customer.get("phone");
            String ddi = phone != null ? (String) phone.getOrDefault("ddi", "") : "";
            String ddd = phone != null ? (String) phone.getOrDefault("ddd", "") : "";
            String number = phone != null ? (String) phone.getOrDefault("number", "") : "";
            String phoneNumber = ddi + ddd + number;

            System.out.println("Evento recebido: " + email);

            UserEntity userEntity = this.searchUserByEmailUseCase.execute(email);

            if (userEntity != null) {
                
                this.activateUserUseCase.execute(userEntity.getEmail());

                return ResponseEntity.ok().body("Usuario ativado com sucesso!");

            }else{
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
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro no processamento do webhook: " + e.getMessage());
        }
    }

    @PostMapping(value = "/canceled")
    public ResponseEntity<String> receiveCanceled(@RequestBody Map<String, Object> payload) {
        try {

            String receivedToken = (String) payload.get("token");

            if (!webhookToken.equals(receivedToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido.");
            }

            String status = (String) payload.get("status");

            if (!status.contains("canceled")) {
                return ResponseEntity.ok("Status nao disponivel para essa operacao.");
            }

            Map<String, Object> urlParams = (Map<String, Object>) payload.getOrDefault("url_params", Map.of());
            Map<String, Object> queryParams = (Map<String, Object>) urlParams.getOrDefault("query_params", Map.of());
            String code = (String) queryParams.get("code");

            System.out.println("Evento recebido: " + code);

            Map<String, Object> customer = (Map<String, Object>) payload.get("customer");

            String email = (String) customer.get("email");

            return userResource.inativateUser(email);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro no processamento do webhook: " + e.getMessage());
        }
    }

}
