package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.model.ResultsCountProjection;
import api.bank.domain.dataprovider.ConsultationEventsDataProvider;
import api.bank.domain.dataprovider.ExecutorDataProvider;
import api.bank.domain.dto.ResultsCounterExecutionDTO;
import api.bank.domain.dto.FindCsvStatusDTO;
import api.bank.domain.entity.ExecutorEntity;
import api.bank.domain.usecase.CsvStatusCheckerUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CsvStatusCheckerService implements CsvStatusCheckerUseCase {

    private final ExecutorDataProvider executorDataProvider;
    private final ConsultationEventsDataProvider consultationEventsDataProvider;

    @Override
    public FindCsvStatusDTO execute(String email, String bankId) {
        ExecutorEntity executor = this.executorDataProvider.findCurrentProcessStatusByEmailAndBankId(email, bankId);
        ResultsCountProjection result = this.consultationEventsDataProvider.getResultsCounterByCsvId(executor.getId());
        Integer quantityDocuments = this.consultationEventsDataProvider.findQuantityOfDocuments(executor.getId());

        ResultsCounterExecutionDTO resultCounts = new ResultsCounterExecutionDTO();
        resultCounts.setTotalConsultas(result.getTotal_consultas());
        resultCounts.setComSaldo(result.getCom_saldo());
        resultCounts.setErro(result.getErro());
        resultCounts.setNaoAutorizado(result.getNao_autorizado());
        resultCounts.setSemSaldo(result.getSem_saldo());

        FindCsvStatusDTO statusInfo = new FindCsvStatusDTO();
        statusInfo.setQuantidadeDocumentos(quantityDocuments);
        statusInfo.setIdCsv(executor.getId());
        statusInfo.setStatus(executor.getProcessStatus().toString());
        statusInfo.setResults(resultCounts);
        
        return statusInfo;
    }

}
