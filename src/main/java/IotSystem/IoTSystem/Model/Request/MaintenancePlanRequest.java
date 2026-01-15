package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceScope;
import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceStatus;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class MaintenancePlanRequest {
    private MaintenanceScope scope;
    private UUID targetId;
    private Date scheduledDate;
    private MaintenanceStatus status;
    private String createdBy;
    private String reason;
}
