package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.KitComponentType;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String seriNumber;
    private String link;
    private UUID kitId;
    private String kitName;

}
