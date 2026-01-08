package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceIssueType;
import lombok.Data;

import java.util.UUID;

@Data
public class MaintenanceIssueRequest {
    private MaintenanceIssueType issueType;
    private Integer quantity;
    private UUID maintenancePlanId;
}
