package api.security.auth.app.entrypoints;

import org.springframework.http.ResponseEntity;

import api.security.auth.app.restmodel.ChangePasswordRestModel;
import api.security.auth.app.restmodel.ChangeUserPasswordRequestDTO;
import api.security.auth.app.restmodel.UserRestModel;

public interface UserResource {

    ResponseEntity<String> registerUser(UserRestModel restModel);

    ResponseEntity<String> changeUserPassword(ChangePasswordRestModel restmodel);
    
    ResponseEntity<String> recoverUserPassword(String email);

    ResponseEntity<String> renewUserPassword(String token, ChangeUserPasswordRequestDTO dto);

    ResponseEntity<String> removeUser(String email);

    ResponseEntity<String> inativateUser(String email);
}
