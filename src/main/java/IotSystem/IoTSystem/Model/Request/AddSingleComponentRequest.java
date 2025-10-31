package IotSystem.IoTSystem.Model.Request;

import lombok.Data;

import java.util.UUID;

@Data
public class AddSingleComponentRequest {
    private UUID kitId;
    private KitComponentRequest component;
}
