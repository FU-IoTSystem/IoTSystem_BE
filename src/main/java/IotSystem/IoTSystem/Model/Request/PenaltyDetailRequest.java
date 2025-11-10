package IotSystem.IoTSystem.Model.Request;

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
public class PenaltyDetailRequest {
    private UUID id;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private UUID policiesId;
    private UUID penaltyId;
}

