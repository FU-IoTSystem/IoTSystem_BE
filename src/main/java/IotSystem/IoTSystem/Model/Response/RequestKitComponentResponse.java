package IotSystem.IoTSystem.Model.Response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RequestKitComponentResponse {
    private UUID id;
    private UUID requestId;
    private UUID kitComponentsId;
    private Integer quantity;
    private String componentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Component details
    private String componentType;
    private String description;
    private Double price;
}

