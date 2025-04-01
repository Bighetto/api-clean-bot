package api.security.auth.domain.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import api.security.auth.domain.usecase.SendRecoverUserPasswordEmailUseCase;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SendRecoverUserPasswordEmailService implements SendRecoverUserPasswordEmailUseCase {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Override
    public void execute(String name, String to, String token) {

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("nome", name);
            context.setVariable("email", to);
            context.setVariable("token", token);
            context.setVariable("portalLink", "https://www.google.com.br/");

            String htmlContent = templateEngine.process("recover-password-email", context);

            helper.setTo(to);
            helper.setSubject("Recuperação de senha");
            helper.setText(htmlContent, true);
            helper.setFrom("routinelove36@gmail.com");

            mailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
