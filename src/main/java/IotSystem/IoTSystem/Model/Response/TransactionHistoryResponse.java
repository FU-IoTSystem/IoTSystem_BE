package IotSystem.IoTSystem.Model.Response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionHistoryResponse {
    private UUID id;
    private String type; // TOP_UP, PENALTY_PAYMENT, REFUND, RENTAL_DEPOSIT
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For penalties
    private String penaltyNote;
    private UUID penaltyId;
    
    // For rental deposits
    private UUID borrowingRequestId;
    private String kitName;
    private Double depositAmount;

    // user
    private String userName;
    private String email;
}

