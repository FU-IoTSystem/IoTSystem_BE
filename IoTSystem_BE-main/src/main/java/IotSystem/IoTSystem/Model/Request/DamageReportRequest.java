package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageReportRequest {
    private String description;
    private ReportStatus status; // PENDING, APPROVED, REJECTED
    private String generatedByEmail;
    private UUID kitId;
    private UUID borrowRequestId;
    private Double totalDamageValue;
}
