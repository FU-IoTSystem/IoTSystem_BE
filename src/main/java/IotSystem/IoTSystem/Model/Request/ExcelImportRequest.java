package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportRequest {
    private String role; // STUDENT, LECTURER
    private String fileName;
    private String fileContent; // Base64 encoded file content
    private String sheetName; // Optional: specific sheet name to import from. If null, uses first sheet
}
