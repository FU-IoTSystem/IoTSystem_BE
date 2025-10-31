package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.KitComponentType;
import IotSystem.IoTSystem.Model.Entities.Kits;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class KitComponentRequest {
    private String componentName;
    private KitComponentType componentType;
    private String description;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Double pricePerCom;
    private String status;
    private String imageUrl;
    private UUID kitId;
}
