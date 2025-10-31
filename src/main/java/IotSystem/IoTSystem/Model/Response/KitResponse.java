package IotSystem.IoTSystem.Model.Response;
import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Data
public class KitResponse {
    private UUID id;
    private String kitName;
    private KitType type;
    private String status;
    private String description;
    private String imageUrl;
    private Integer quantityTotal;
    private Integer quantityAvailable;

    private List<KitComponentResponse> components;
}