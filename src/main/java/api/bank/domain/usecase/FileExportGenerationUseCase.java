package api.bank.domain.usecase;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FileExportGenerationUseCase {

    ByteArrayInputStream execute(List<String[]> data, String[] headers);

    
} 