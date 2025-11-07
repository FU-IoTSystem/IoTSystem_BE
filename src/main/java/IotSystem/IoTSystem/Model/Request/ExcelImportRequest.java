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
}
