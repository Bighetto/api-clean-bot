package api.security.auth.domain.dataprovider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import api.security.auth.app.converter.UserModelToEntityConverter;
import api.security.auth.app.model.UserLogin;
import api.security.auth.app.provider.AuthProvider;
import api.security.auth.app.repository.UserRepository;

public class AuthDataProviderTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserModelToEntityConverter userModelToEntityConverter;

    AuthDataProvider authDataProvider;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authDataProvider = new AuthProvider(userRepository, userModelToEntityConverter);
    }

    @Test
    void shouldReturnUserLogin() {
        UserLogin userLogin = new UserLogin();
        userLogin.setEmail("tes@email.com");

        when(this.userRepository.findByEmail("test@email.com")).thenReturn(userLogin);

        UserLogin response = this.authDataProvider.findUserLoginByUserEmail("test@email.com");

        assertNotNull(response.getEmail());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

}
