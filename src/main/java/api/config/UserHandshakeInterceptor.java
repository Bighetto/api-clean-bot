package api.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {
    
        String query = request.getURI().getQuery();
        String tempEmail = null; 
    
        if (query != null && query.contains("email=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("email=")) {
                    tempEmail = param.substring("email=".length());
                    break;
                }
            }
        }
    
        if (tempEmail == null || tempEmail.isEmpty()) {
            return false; // rejeita handshake sem email
        }
    
        final String emailFinal = tempEmail;  // variável final para usar no Principal
    
        attributes.put("user", new Principal() {
            @Override
            public String getName() {
                return emailFinal;
            }
        });
    
        return true;
    }
    

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
