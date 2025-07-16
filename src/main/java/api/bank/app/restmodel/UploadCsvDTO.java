package api.bank.app.restmodel;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadCsvDTO {
    private MultipartFile file;
    private String email;
    private String userDocument;
    private String bankId; 
}
