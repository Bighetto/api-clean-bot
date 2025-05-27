// package api.bank.domain.usecase;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.when;

// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.web.client.RestTemplate;

// import api.bank.app.restmodel.BalancePeriods;
// import api.bank.app.restmodel.ConsultV8CustomerBalanceResponse;
// import api.bank.domain.service.ProcessRowService;

// public class ProcessRowUseCaseTest {

//     @Mock
//     private ConsultV8CustomerBalanceUseCase consultUseCase;
//     @Mock
//     private SimulateV8CustomerUseCase simulateUseCase;
    
//     private ProcessRowUseCase processRowUseCase;

//     private final RestTemplate session = new RestTemplate();
//     private final String cpf = "12345678900";
//     private final String token = "token123";

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.openMocks(this);
//         processRowUseCase = new ProcessRowService(consultUseCase, simulateUseCase);
//     }

//     @Test
//     public void testNaoAutorizado() {
//         ConsultV8CustomerBalanceResponse response = new ConsultV8CustomerBalanceResponse();
//         response.setBalancePeriods("NÃO AUTORIZADO");
//         response.setValidCustomer(false);

//         when(consultUseCase.execute(session, token, cpf)).thenReturn(response);

//         String result = processRowUseCase.execute(session, token, cpf);
//         assertEquals("NÃO AUTORIZADO", result);
//     }

//     @Test
//     public void testCpfInvalido() {
//         ConsultV8CustomerBalanceResponse response = new ConsultV8CustomerBalanceResponse();
//         response.setBalancePeriods("CPF INVÁLIDO");
//         response.setValidCustomer(false);

//         when(consultUseCase.execute(session, token, cpf)).thenReturn(response);

//         String result = processRowUseCase.execute(session, token, cpf);
//         assertEquals("CPF INVÁLIDO", result);
//     }

//     @Test
//     public void testComSaldoERetornaSimulacao() {
//         BalancePeriods period = new BalancePeriods(1000.0, "2025-06-01");
//         ConsultV8CustomerBalanceResponse response = new ConsultV8CustomerBalanceResponse();
//         response.setBalancePeriods(List.of(period));
//         response.setBalanceId("balance123");
//         response.setValidCustomer(true);

//         when(consultUseCase.execute(session, token, cpf)).thenReturn(response);
//         when(simulateUseCase.execute(session, token, List.of(period), cpf, "balance123")).thenReturn(5000.0);

//         String result = processRowUseCase.execute(session, token, cpf);
//         assertEquals("5000.0", result);
//     }

//     @Test
//     public void testSemSaldo() {
//         ConsultV8CustomerBalanceResponse response = new ConsultV8CustomerBalanceResponse();
//         response.setBalancePeriods("SEM SALDO");
//         response.setValidCustomer(true);

//         when(consultUseCase.execute(session, token, cpf)).thenReturn(response);

//         String result = processRowUseCase.execute(session, token, cpf);
//         assertEquals("SEM SALDO", result);
//     }

//     @Test
//     public void testErroNaConsulta() {
//         when(consultUseCase.execute(session, token, cpf)).thenThrow(new RuntimeException("Erro simulado"));

//         String result = processRowUseCase.execute(session, token, cpf);
//         assertEquals("ERRO NA REQUISIÇÃO", result);
//     }
// }
