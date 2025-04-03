package api.security.auth.app.entrypoints;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.bank.app.converter.PlanModelToEntityConverter;
import api.bank.domain.dataprovider.PlanDataProvider;
import api.bank.domain.entity.PlanEntity;
import api.security.auth.app.converter.UserRestModelToEntityConverter;
import api.security.auth.app.model.UserLogin;
import api.security.auth.app.restmodel.AuthenticationResponseRestModel;
import api.security.auth.app.restmodel.AuthenticationRestModel;
import api.security.auth.app.restmodel.ChangePasswordRestModel;
import api.security.auth.app.restmodel.UserRestModel;
import api.security.auth.app.security.SecurityConfig;
import api.security.auth.app.security.TokenService;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.enums.UserStatusEnum;
import api.security.auth.domain.usecase.GenerateRecoveryTokenUseCase;
import api.security.auth.domain.usecase.RegisterNewUserUseCase;
import api.security.auth.domain.usecase.SearchUserByEmailUseCase;
import api.security.auth.domain.usecase.SendEmailUseCase;
import api.security.auth.domain.usecase.SendRecoverUserPasswordEmailUseCase;
import api.security.auth.domain.usecase.UpdatePasswordUserLoginUseCase;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping(value = "/user")
@AllArgsConstructor
@CrossOrigin
public class UserController implements UserResource {

    private final SearchUserByEmailUseCase searchUserByEmailUseCase;
    private final RegisterNewUserUseCase registerNewUserUseCase;
    private final UserRestModelToEntityConverter userRestModelToEntityConverter;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final SecurityConfig securityConfig;
    private final SendEmailUseCase sendEmailUseCase;
    private final PlanDataProvider planDataProvider;
    private final PlanModelToEntityConverter planConverter;
    private final UpdatePasswordUserLoginUseCase updatePasswordUserLoginUseCase;
    private final GenerateRecoveryTokenUseCase generateTokenUseCase;
    private final SendRecoverUserPasswordEmailUseCase sendRecoverUserPasswordEmailUseCase;


    @Override
    @PostMapping("/create")
    public ResponseEntity<String> registerUser(@RequestBody UserRestModel restModel) {

        try{
            UserEntity entity = this.userRestModelToEntityConverter.convertToEntity(restModel);

            String encryptedPassword = this.securityConfig.passwordEncoder().encode(entity.getDocument());

            PlanEntity plan = this.planDataProvider.findPlanById(restModel.getPlanId());
            
            UserLogin model = new UserLogin(
                entity.getDocument(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                "cliente",
                encryptedPassword,
                LocalDateTime.now(),
                planConverter.convertToModel(plan),
                new ArrayList<>(),
                UserStatusEnum.ATIVO    
            );

            this.registerNewUserUseCase.execute(model);

            this.sendEmailUseCase.execute(entity.getEmail(), entity.getName(), entity.getDocument());
            
            return ResponseEntity.ok().body("The new user has been registered!");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
        }
        
    }

    @Override
    @PostMapping("/update/password")
    public ResponseEntity<String> changeUserPassword(@RequestBody ChangePasswordRestModel restModel) {
    try {
        UserEntity user = this.searchUserByEmailUseCase.execute(restModel.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        String encryptedPassword = this.securityConfig.passwordEncoder().encode(restModel.getPassword());
        
        this.updatePasswordUserLoginUseCase.execute(user.getDocument(), encryptedPassword);

        return ResponseEntity.ok().body("Password updated successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password. Please try again later.");
    }
}


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseRestModel> createAuthenticationToken(@RequestBody AuthenticationRestModel restModel) {
        
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(restModel.getEmail(), restModel.getPassword());

            var auth = this.authenticationManager.authenticate(usernamePassword);

            var userDetails = (UserLogin)auth.getPrincipal();
            var nome = userDetails.getName();
            var email = userDetails.getEmail();
            var role = userDetails.getTipo();
    
            final String jwt = tokenService.generateToken(userDetails);

            AuthenticationResponseRestModel responseRestModel = new AuthenticationResponseRestModel(email, nome, jwt, role);
    
            return ResponseEntity.ok(responseRestModel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    @PostMapping("/recoverPassword/{userEmail}")
    public ResponseEntity<String> recoverUserPassword(@PathVariable String userEmail) {

        try {
            UserEntity user = this.searchUserByEmailUseCase.execute(userEmail);
    
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            RecoveryTokenEntity token = this.generateTokenUseCase.execute(userEmail);

            this.sendRecoverUserPasswordEmailUseCase.execute(user.getName(), userEmail, token.getToken());

            return ResponseEntity.ok().body("Password reset email sent successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
