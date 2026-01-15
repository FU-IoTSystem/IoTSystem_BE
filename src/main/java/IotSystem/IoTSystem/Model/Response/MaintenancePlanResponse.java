package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceScope;
import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MaintenancePlanResponse {
    private UUID id;
    private MaintenanceScope scope;
    private java.util.Date scheduledDate;
    private MaintenanceStatus status;
    private String createdBy;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
