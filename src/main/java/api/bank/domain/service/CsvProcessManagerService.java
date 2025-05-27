package api.bank.domain.service;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.model.BankUser;
import api.bank.app.model.ConsultationEvents;
import api.bank.app.repository.ConsultationEventsRepository;
import api.bank.app.repository.ExecutorRepository;
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
    private final ExecutorRepository executorRepository;
    private final ConsultationEventsRepository eventsRepository;

    private final ConcurrentMap<String, List<Future<?>>> processosSubTasks = new ConcurrentHashMap<>();

    @Override
    public String iniciarProcessamento(String csvId, List<String> usuarios, String email, LogSenderUseCase logSender) {

        String processoId = UUID.randomUUID().toString();

        List<Future<?>> subtasksFutures = new CopyOnWriteArrayList<>();
        processosSubTasks.put(processoId, subtasksFutures);

        executorRepository.updateProcessStatusByCsvId(csvId, ProcessStatus.EM_ANDAMENTO);

        Future<?> future = executor.submit(() -> {
            boolean processoCompletado = false;

            try {
                List<ConsultationEvents> registros = repository.findValidByCsvId(csvId);
                int total = registros.size();
                int usuariosCount = usuarios.size();
                int porUsuario = total / usuariosCount;
                int resto = total % usuariosCount;
                

                logSender.enviarLog("Início do processamento. Total de registros: " + total, email);

                executorRepository.updateProcessStatusByCsvId(csvId, ProcessStatus.EM_ANDAMENTO);

                CountDownLatch latch = new CountDownLatch(usuariosCount);

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

                    Future<?> subtaskFuture  = executor.submit(() -> {
                        try {
                            int processed = 0;
                            for (ConsultationEvents registro : subLista) {
                                if (Thread.currentThread().isInterrupted()) return;

                                String result = this.processRowUseCase.execute(restTemplate, token, registro.getDocumentClient());
                                registro.setValueResult(result);
                                repository.save(registro);

                                logSender.enviarLog("Busca efetuada para o cliente: " + registro.getDocumentClient() + ". Resultado: " + result, email);

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }

                                processed++;
                            }
                            logSender.enviarLog("Finalizou o processamento de " + processed + " registros.", email);
                        } finally {
                            latch.countDown();
                        }
                    });

                    subtasksFutures.add(subtaskFuture);

                    start = end;
                }

                new Thread(() -> {
                    try {
                        latch.await(); 
                        Integer countEventsNull = this.eventsRepository.countResultsNull(csvId);
                        if (countEventsNull > 0) {
                            executorRepository.updateProcessStatusByCsvId(csvId, ProcessStatus.PENDENTE);
                            logSender.enviarLog("Processamento pausado com sucesso!", email);
                        }else{
                            executorRepository.updateProcessStatusByCsvId(csvId, ProcessStatus.ENCERRADO);
                            logSender.enviarLog("Processamento concluído com sucesso!", email);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logSender.enviarLog("Thread de atualização de status foi interrompida.", email);
                    }
                    limparProcesso(processoId, csvId);
                }).start();

                processoCompletado = true;

            } catch (Exception e) {
                logSender.enviarLog("[" + processoId + "] Erro no processamento: " + e.getMessage(), email);
                executorRepository.updateProcessStatusByCsvId(csvId, ProcessStatus.EM_ANDAMENTO);
                limparProcesso(processoId, csvId);

            }
        });

        processos.put(processoId, future);
        return processoId;
    }

    @Override
    public boolean pararProcessamento(String processoId) {
        Future<?> future = processos.get(processoId);
        List<Future<?>> subtasks = processosSubTasks.get(processoId);

        boolean cancelouPrincipal = false;
        boolean cancelouSubtasks = true;

        if (future != null) {
            cancelouPrincipal = future.cancel(true);
            processos.remove(processoId);
        }

        if (subtasks != null) {
            for (Future<?> subtask : subtasks) {
                if (subtask != null && !subtask.isDone()) {
                    boolean cancelou = subtask.cancel(true);
                    cancelouSubtasks = cancelouSubtasks && cancelou;
                }
            }
            processosSubTasks.remove(processoId);
        }

        return cancelouPrincipal && cancelouSubtasks;
    }

    private void limparProcesso(String processoId, String csvId) {
        processos.remove(processoId);
        processosSubTasks.remove(processoId);
    }

}
