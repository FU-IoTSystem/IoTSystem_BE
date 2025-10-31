package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Data;

@Data
public class KitSingleCreateRequest {
    private String kitName;
    private KitType type;
    private String status;
    private String description;
    private String imageUrl;
    private Integer quantityTotal;
    private Integer quantityAvailable;
}
