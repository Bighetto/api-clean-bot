package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.restmodel.ProcessamentoCsvRestModel;
import api.bank.domain.dto.ProcessamentoCsvDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProcessamentoCsvRestModelToDtoConverter {

    public ProcessamentoCsvDTO convertToDto(ProcessamentoCsvRestModel model) {
        ProcessamentoCsvDTO dto = new ProcessamentoCsvDTO();
        dto.setCsvId(model.getCsvId());
        dto.setEmail(model.getEmail());
        dto.setBankId(model.getBankId());
        dto.setUsuarios(model.getUsuarios());

        return dto;
    }

}
