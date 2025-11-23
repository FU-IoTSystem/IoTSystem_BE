package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportResponse {
    private boolean success;
    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<String> errors;
    private String message;
}
