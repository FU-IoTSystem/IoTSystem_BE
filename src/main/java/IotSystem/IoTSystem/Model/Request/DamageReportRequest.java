package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.ReportStatus;
import lombok.*;

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
