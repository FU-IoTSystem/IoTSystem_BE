package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitComponentHistoryRequest {
    private UUID kitId;
    private UUID componentId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private String imgUrl;
    private UUID penaltyDetailId;
}

