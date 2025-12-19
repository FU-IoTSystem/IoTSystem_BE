package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitComponentHistoryResponse {
    private UUID id;
    private UUID kitId;
    private String kitName;
    private UUID componentId;
    private String componentName;
    private String action;
    private String oldStatus;
    private String newStatus;
    private String note;
    private UUID penaltyDetailId;
    private String penaltyDetailImageUrl;
    private LocalDateTime createdAt;
}

