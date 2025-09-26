package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.ReportStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageReportRequest {
    private String description;
    private ReportStatus status; // PENDING, APPROVED, REJECTED
    private String generatedByEmail; // hoặc generatedById nếu bạn muốn gán user
}
