package api.config;

import org.springframework.context.annotation.Configuration;

import api.bank.app.enums.ProcessStatus;
import api.bank.app.repository.ExecutorRepository;
import jakarta.annotation.PostConstruct;

@Configuration
public class ShutdownHandler {

    private final ExecutorRepository executorRepository;

    public ShutdownHandler(ExecutorRepository executorRepository) {
        this.executorRepository = executorRepository;
    }

    @PostConstruct
    public void onStartup() {
        executorRepository.updateAllStatusEmAndamentoToPendente(ProcessStatus.PENDENTE, ProcessStatus.EM_ANDAMENTO);
    }
}

