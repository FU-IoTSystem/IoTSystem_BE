package IotSystem.IoTSystem.Model.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestKitComponentRequest {
    private UUID requestId;
    private UUID kitComponentsId;
    private Integer quantity;
    private String componentName;
}

