package api.bank.domain.service;

import api.bank.app.model.BankUser;
import api.bank.app.model.ConsultationEvents;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.GetUserBankV8TokenUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import api.bank.domain.usecase.ProcessRowUseCase;
import api.security.auth.app.security.AESEncryptor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

import javax.naming.NameNotFoundException;

@Component
@AllArgsConstructor
public class CsvProcessManagerService implements CsvProcessManagerUseCase {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final Map<String, Future<?>> processos = new ConcurrentHashMap<>();
    private final ConsultationEventsRepository repository;
    private final BankUserDataProvider bankUserDataProvider;
    private final GetUserBankV8TokenUseCase getTokenUseCase;
    private final ProcessRowUseCase processRowUseCase;
    private final CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;
    private final AESEncryptor aesEncryptor;

    @Override
    public String iniciarProcessamento(String csvId, List<String> usuarios, LogSenderUseCase logSender) {
        String processoId = UUID.randomUUID().toString();

        Future<?> future = executor.submit(() -> {
            try {
                List<ConsultationEvents> registros = repository.findByCsvId_Id(csvId);
                int total = registros.size();
                int usuariosCount = usuarios.size();
                int porUsuario = total / usuariosCount;
                int resto = total % usuariosCount;

                logSender.enviarLog("Início do processamento. Total de registros: " + total);

                int start = 0;
                for (int i = 0; i < usuariosCount; i++) {
                    int count = porUsuario + (i < resto ? 1 : 0);
                    int end = start + count;
                    final List<ConsultationEvents> subLista = registros.subList(start, end);
                    final String usuario = usuarios.get(i);

                    Optional<BankUser> user = this.bankUserDataProvider.findBankUserById(usuario);

                    if (user.isEmpty()) throw new NameNotFoundException("Usuario nao encontrado.");

                    String password = this.aesEncryptor.decrypt(user.get().getPassword());

                    String token = this.getTokenUseCase.execute(user.get().getLogin(), password);

                    RestTemplate restTemplate = this.createRestTemplateSessionUseCase.execute();

                    executor.submit(() -> {
                        int processed = 0;
                        for (ConsultationEvents registro : subLista) {
                            if (Thread.currentThread().isInterrupted()) break;

                            String result = this.processRowUseCase.execute(restTemplate, token, registro.getDocumentClient());
                            registro.setValueResult(result);

                            repository.save(registro);

                            logSender.enviarLog("Busca efetuada para o cliente: " + registro.getDocumentClient() + ". Resultado: " + result);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }

                            processed++;
                        }
                        logSender.enviarLog("Finalizou o processamento de " + processed + " registros.");
                    });

                    start = end;
                }

            } catch (Exception e) {
                logSender.enviarLog("[" + processoId + "] Erro no processamento: " + e.getMessage());
            } finally {
                processos.remove(processoId);
            }
        });

        processos.put(processoId, future);
        return processoId;
    }


    @Override
    public boolean pararProcessamento(String processoId) {
        Future<?> future = processos.get(processoId);
        if (future != null) {
            future.cancel(true);
            processos.remove(processoId);
            return true;
        }
        return false;
    }
}
