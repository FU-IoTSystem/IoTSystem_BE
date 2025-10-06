package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.KitComponentType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class KitComponentResponse {
    private UUID id;
    private String componentName;
    private KitComponentType componentType;
    private String description;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Double pricePerCom;
    private String status;
    private String imageUrl;
    private UUID kitId;
    private String kitName;

}
