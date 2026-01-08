package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceIssueType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MaintenanceIssueResponse {
    private UUID id;
    private MaintenanceIssueType issueType;
    private UUID maintenancePlanId;
    private Integer quantity;
}
