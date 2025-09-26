package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KitComponentRequest {
    private String componentName;
    private Kit_Items_Types componentType;
    private String description;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Double pricePerCom;
    private String status;
    private String imageUrl;
    private UUID kitId; // tham chiếu đến kit cha
}
