package api.bank.app.restmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadCsvDataRestModel {

    private String email;
    private String bankName;

}
