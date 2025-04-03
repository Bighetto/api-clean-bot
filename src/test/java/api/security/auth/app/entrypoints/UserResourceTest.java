package api.security.auth.app.entrypoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import api.bank.app.converter.PlanModelToEntityConverter;
import api.bank.domain.dataprovider.PlanDataProvider;
import api.security.auth.app.converter.UserRestModelToEntityConverter;
import api.security.auth.app.security.SecurityConfig;
import api.security.auth.app.security.TokenService;
import api.security.auth.domain.entity.RecoveryTokenEntity;
import api.security.auth.domain.entity.UserEntity;
import api.security.auth.domain.usecase.GenerateRecoveryTokenUseCase;
import api.security.auth.domain.usecase.RegisterNewUserUseCase;
import api.security.auth.domain.usecase.SearchUserByEmailUseCase;
import api.security.auth.domain.usecase.SendEmailUseCase;
import api.security.auth.domain.usecase.SendRecoverUserPasswordEmailUseCase;
import api.security.auth.domain.usecase.UpdatePasswordUserLoginUseCase;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {

    @Mock
    private SearchUserByEmailUseCase searchUserByEmailUseCase;
    @Mock
    private RegisterNewUserUseCase registerNewUserUseCase;
    @Mock
    private UserRestModelToEntityConverter userRestModelToEntityConverter;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private SecurityConfig securityConfig;
    @Mock
    private SendEmailUseCase sendEmailUseCase;
    @Mock
    private PlanDataProvider planDataProvider;
    @Mock
    private PlanModelToEntityConverter planConverter;
    @Mock
    private UpdatePasswordUserLoginUseCase updatePasswordUserLoginUseCase;
    @Mock
    private GenerateRecoveryTokenUseCase generateTokenUseCase;
    @Mock
    private SendRecoverUserPasswordEmailUseCase sendRecoverUserPasswordEmailUseCase;

    UserResource controller;

    @BeforeEach
    void setup() {
        controller = new UserController(searchUserByEmailUseCase, registerNewUserUseCase, userRestModelToEntityConverter, authenticationManager, tokenService, securityConfig, sendEmailUseCase, planDataProvider, planConverter, updatePasswordUserLoginUseCase, generateTokenUseCase, sendRecoverUserPasswordEmailUseCase);
    }

    @Test
    void shouldReturnStringWithSucessful() {
        String userEmail = "TestEmail";

        UserEntity user = new UserEntity();
        user.setEmail(userEmail);
        user.setName("TestName");

        RecoveryTokenEntity token = new RecoveryTokenEntity();
        token.setToken("TestToken");

        when(this.searchUserByEmailUseCase.execute(userEmail)).thenReturn(user);
        when(this.generateTokenUseCase.execute(userEmail)).thenReturn(token);
        doNothing().when(sendRecoverUserPasswordEmailUseCase).execute(user.getName(), userEmail, token.getToken());

        ResponseEntity<String> response = this.controller.recoverUserPassword(userEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset email sent successfully", response.getBody());

        verify(searchUserByEmailUseCase, times(1)).execute(userEmail);
        verify(generateTokenUseCase, times(1)).execute(userEmail);
        verify(sendRecoverUserPasswordEmailUseCase, times(1)).execute(user.getName(), userEmail, token.getToken());
    }
    
    @Test
    void shouldReturnHttpStatusNotFound() {

        when(searchUserByEmailUseCase.execute(anyString())).thenReturn(null);

        ResponseEntity<String> response = this.controller.recoverUserPassword("NotFound");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenTokenGenerationFails() {
        String email = "test@email.com";
        UserEntity user = new UserEntity(); 
        user.setName("Test User");

        when(searchUserByEmailUseCase.execute(email)).thenReturn(user);
        when(generateTokenUseCase.execute(email)).thenThrow(new RuntimeException("Token error"));

        ResponseEntity<?> response = controller.recoverUserPassword(email);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
