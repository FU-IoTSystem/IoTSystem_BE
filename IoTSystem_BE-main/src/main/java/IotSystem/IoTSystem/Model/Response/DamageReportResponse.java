package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageReportResponse {
    private UUID id;
    private String description;
    private ReportStatus status;
    private String generatedByEmail;
    private UUID kitId;
    private UUID borrowRequestId;
    private Double totalDamageValue;
    private Date createdAt;
    private Date updatedAt;
}
