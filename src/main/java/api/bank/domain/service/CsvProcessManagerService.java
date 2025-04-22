package api.bank.domain.service;

import api.bank.app.model.ConsultationEvents;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@AllArgsConstructor
public class CsvProcessManagerService implements CsvProcessManagerUseCase {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final Map<String, Future<?>> processos = new ConcurrentHashMap<>();
    private final ConsultationEventsRepository repository;

    @Override
    public String iniciarProcessamento(String csvId, List<String> usuarios, LogSenderUseCase logSender) {
        String processoId = UUID.randomUUID().toString();

        Future<?> future = executor.submit(() -> {
            try {
                List<ConsultationEvents> registros = repository.findByCsvId(csvId);
                int total = registros.size();
                int usuariosCount = usuarios.size();
                int porUsuario = total / usuariosCount;
                int resto = total % usuariosCount;

                logSender.enviarLog("[" + processoId + "] Início do processamento. Total de registros: " + total);

                int start = 0;
                for (int i = 0; i < usuariosCount; i++) {
                    int count = porUsuario + (i < resto ? 1 : 0);
                    int end = start + count;
                    final List<ConsultationEvents> subLista = registros.subList(start, end);
                    final String usuario = usuarios.get(i);

                    executor.submit(() -> {
                        int processed = 0;
                        for (ConsultationEvents registro : subLista) {
                            if (Thread.currentThread().isInterrupted()) break;

                            logSender.enviarLog("[" + processoId + "][Usuário " + usuario + "] Processando: " + registro.getDocumentClient());
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }

                            processed++;
                        }
                        logSender.enviarLog("[" + processoId + "][Usuário " + usuario + "] Finalizou " + processed + " registros.");
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
