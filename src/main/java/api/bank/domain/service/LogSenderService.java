package api.bank.domain.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import api.bank.domain.usecase.LogSenderUseCase;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogSenderService implements LogSenderUseCase {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void enviarLog(String mensagem, String username) {
        messagingTemplate.convertAndSendToUser(username, "/queue/logs", mensagem);
    }
    
}
