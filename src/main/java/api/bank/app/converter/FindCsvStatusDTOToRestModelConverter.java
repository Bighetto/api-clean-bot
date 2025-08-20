package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.FindCsvStatusRestModel;
import api.bank.app.restmodel.ResultsCounterExecutionRestModel;
import api.bank.domain.dto.FindCsvStatusDTO;
import api.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FindCsvStatusDTOToRestModelConverter extends ConvertCase<FindCsvStatusDTO, FindCsvStatusRestModel> {

    private final ResultsCounterExecutionDTOToRestModelConverter resultsCounterDTOToRestModelConverter;

    @Override
    public FindCsvStatusRestModel convertToModel(FindCsvStatusDTO dto) {
        
        ResultsCounterExecutionRestModel resultsRestModel = resultsCounterDTOToRestModelConverter.convertToModel(dto.getResults());

        return FindCsvStatusRestModel.builder()
                .idCsv(dto.getIdCsv())
                .status(dto.getStatus())
                .quantidadeDocumentos(dto.getQuantidadeDocumentos())
                .results(resultsRestModel)
                .build();
    }

    @Override
    public FindCsvStatusDTO convertToEntity(FindCsvStatusRestModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToEntity'");
    }

}
