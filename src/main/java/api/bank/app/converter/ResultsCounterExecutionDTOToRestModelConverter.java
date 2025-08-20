package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.ResultsCounterExecutionRestModel;
import api.bank.domain.dto.ResultsCounterExecutionDTO;
import api.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ResultsCounterExecutionDTOToRestModelConverter extends ConvertCase<ResultsCounterExecutionDTO, ResultsCounterExecutionRestModel> {

    @Override
    public ResultsCounterExecutionRestModel convertToModel(ResultsCounterExecutionDTO dto) {
        ResultsCounterExecutionRestModel model = new ResultsCounterExecutionRestModel();
        model.setTotal_consultas(dto.getTotalConsultas());
        model.setCom_saldo(dto.getComSaldo());
        model.setErro(dto.getErro());
        model.setNao_autorizado(dto.getNaoAutorizado());
        model.setSem_saldo(dto.getSemSaldo());

        return model;
    }

    @Override
    public ResultsCounterExecutionDTO convertToEntity(ResultsCounterExecutionRestModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToEntity'");
    }

}
