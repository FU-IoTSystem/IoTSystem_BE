package IotSystem.IoTSystem.Model.Request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddMultipleComponentsRequest {
    private UUID kitId;
    private List<KitComponentRequest> components;
}
