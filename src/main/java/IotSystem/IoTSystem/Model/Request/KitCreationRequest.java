package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Data;

import java.util.List;

@Data
public class KitCreationRequest {
    private String kitName;
    private KitType type;
    private String status;
    private String description;
    private String imageUrl;
    private Integer quantityTotal;
    private Integer quantityAvailable;

    private List<KitComponentRequest> components;
}
