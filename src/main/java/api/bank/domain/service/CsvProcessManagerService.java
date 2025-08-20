package api.bank.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.NameNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.exception.StartCsvProcessingException;
import api.bank.app.model.BankUser;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.dto.UpdateValueResultDTO;
import api.bank.domain.entity.ConsultationEventsEntity;
import api.bank.domain.usecase.CreateRestTemplateSessionUseCase;
import api.bank.domain.usecase.CsvProcessManagerUseCase;
import api.bank.domain.usecase.GetUserBankHubTokenUseCase;
import api.bank.domain.usecase.GetUserBankV8TokenUseCase;
import api.bank.domain.usecase.LogSenderUseCase;
import api.bank.domain.usecase.ProcessRowUseCase;
import api.bank.domain.usecase.UpdateCsvIdProcessUseCase;
import api.bank.domain.usecase.UpdateCsvStatusUseCase;
import api.security.auth.app.security.AESEncryptor;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CsvProcessManagerService implements CsvProcessManagerUseCase {

    private final ExecutorService executor = Executors.newFixedThreadPool(15);
    private final Map<String, Future<?>> processos = new ConcurrentHashMap<>();
    private final ConsultationEventsDataProvider consultationEventsDataProvider;
    private final BankUserDataProvider bankUserDataProvider;
    private final GetUserBankV8TokenUseCase getTokenUseCase;
    private final GetUserBankHubTokenUseCase getUserBankHubTokenUseCase;
    private final ProcessRowHubBankService processRowHubBankService;
    private final ProcessRowUseCase processRowUseCase;
    private final CreateRestTemplateSessionUseCase createRestTemplateSessionUseCase;
    private final AESEncryptor aesEncryptor;
    private final UpdateCsvStatusUseCase updateCsvStatusService;
    private final UpdateCsvIdProcessUseCase updateCsvIdProcessService;

    private final ConcurrentMap<String, List<Future<?>>> processosSubTasks = new ConcurrentHashMap<>();

    @Override
    public String iniciarProcessamento(String csvId, List<String> usuarios, String email, LogSenderUseCase logSender,
            String bankName) {

        Page<ConsultationEventsEntity> paginaInicial = consultationEventsDataProvider.findValidByCsvId(csvId,
                PageRequest.of(0, 1));
        if (paginaInicial.isEmpty()) {
            throw new StartCsvProcessingException(
                    "O CSV com ID " + csvId + " está vazio ou não contém registros válidos.");
        }
        String bancoDoCsvId = paginaInicial.getContent().get(0).getBankId();

        try {
            validarUsuariosDoBanco(bancoDoCsvId, usuarios);
        } catch (NameNotFoundException | IllegalStateException e) {
            logSender.enviarLog("Falha na validação antes de iniciar o processamento: " + e.getMessage(), email,
                    bankName);
            throw new StartCsvProcessingException("Erro de validação: " + e.getMessage());
        }

        String processoId = UUID.randomUUID().toString();
        System.out.println(processoId);

        List<Future<?>> subtasksFutures = new CopyOnWriteArrayList<>();
        processosSubTasks.put(processoId, subtasksFutures);

        this.updateCsvIdProcessService.execute(csvId, processoId);
        this.updateCsvStatusService.execute(csvId, ProcessStatus.EM_ANDAMENTO);

        Future<?> future = executor.submit(() -> {

            int pageSize = 500;
            int pageNumber = 0;
            Page<ConsultationEventsEntity> page;

            try {
                logSender.enviarLog("Início do processamento.", email, bankName);

                do {
                    page = consultationEventsDataProvider.findValidByCsvId(csvId, PageRequest.of(pageNumber, pageSize));
                    List<ConsultationEventsEntity> registros = page.getContent();
                    int total = registros.size();
                    int usuariosCount = usuarios.size();
                    int porUsuario = total / usuariosCount;
                    int resto = total % usuariosCount;

                    CountDownLatch latch = new CountDownLatch(usuariosCount);

                    int start = 0;
                    for (int i = 0; i < usuariosCount; i++) {

                        int count = porUsuario + (i < resto ? 1 : 0);
                        int end = start + count;

                        if (start >= end) {
                            latch.countDown();
                            continue;
                        }

                        final List<ConsultationEventsEntity> subLista = registros.subList(start, end);
                        final String usuario = usuarios.get(i);

                        Optional<BankUser> user = this.bankUserDataProvider.findBankUserById(usuario);
                        if (user.isEmpty())
                            throw new NameNotFoundException("Usuario nao encontrado.");

                        Future<?> subtaskFuture = executor.submit(() -> {
                            try {
                                for (ConsultationEventsEntity registro : subLista) {
                                    if (Thread.currentThread().isInterrupted())
                                        return;
                                    String token;
                                    String result = "NÃO AUTORIZADO";
                                    String password = this.aesEncryptor.decrypt(user.get().getPassword());
                                    RestTemplate restTemplate = this.createRestTemplateSessionUseCase.execute();

                                    switch (registro.getBankId()) {
                                        case "V8 Digital":
                                            token = this.getTokenUseCase.execute(user.get().getLogin(), password);
                                            result = this.processRowUseCase.execute(restTemplate, token,
                                                    registro.getDocumentClient());
                                            registro.setValueResult(result);
                                            break;
                                        case "Hub Credito":
                                            token = this.getUserBankHubTokenUseCase.execute(user.get().getLogin(),
                                                    password);
                                            result = this.processRowHubBankService.execute(restTemplate, token,
                                                    registro.getDocumentClient());
                                            registro.setValueResult(result);
                                            break;
                                        default:
                                            logSender.enviarLog("Processamento não efetuado", email, bankName);
                                            updateCsvStatusService.execute(csvId, ProcessStatus.PENDENTE);
                                            limparProcesso(processoId, csvId);
                                            break;
                                    }

                                    UpdateValueResultDTO dto = new UpdateValueResultDTO();
                                    dto.setId(registro.getId());
                                    dto.setValueResult(result);

                                    consultationEventsDataProvider.updateValueResult(dto);

                                    logSender.enviarLog("Busca efetuada para o cliente: " + registro.getDocumentClient()
                                            + ". Resultado: " + result, email, bankName);

                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                            } finally {
                                latch.countDown();
                            }
                        });

                        subtasksFutures.add(subtaskFuture);

                        start = end;
                    }
                    latch.await();
                    pageNumber++;

                } while (page.hasNext());

                new Thread(() -> {
                    try {
                        Integer countEventsNull = this.consultationEventsDataProvider.countResultsNull(csvId);
                        if (countEventsNull > 0) {
                            updateCsvStatusService.execute(csvId, ProcessStatus.PENDENTE);
                            logSender.enviarLog("Processamento pausado com sucesso!", email, bankName);
                        } else {
                            updateCsvStatusService.execute(csvId, ProcessStatus.ENCERRADO);
                            logSender.enviarLog("Processamento concluído com sucesso!", email, bankName);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                        logSender.enviarLog("Thread de atualização de status foi interrompida.", email, bankName);
                    }
                    limparProcesso(processoId, csvId);
                }).start();

            } catch (Exception e) {
                logSender.enviarLog("Pausa no processamento.", email, bankName);
                updateCsvStatusService.execute(csvId, ProcessStatus.PENDENTE);
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

    private void validarUsuariosDoBanco(String targetBankId, List<String> usuarios) throws NameNotFoundException {
        for (String userId : usuarios) {
            BankUser user = this.bankUserDataProvider.findBankUserById(userId)
                    .orElseThrow(() -> new NameNotFoundException("Usuário com ID " + userId + " não encontrado."));

            String userBankId = user.getBank().getName();

            if (!userBankId.equals(targetBankId)) {
                throw new IllegalStateException("O usuário " + user.getLogin() + " (Banco ID: " + userBankId +
                        ") não pertence ao banco do CSV (Banco ID: " + targetBankId + ").");
            }
        }
    }

}
