package IotSystem.IoTSystem.Model.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KitRequest { //cho school staff va admin
    private String kitName;
    private String type;
    private String status;
    private String description;
    private String imageUrl;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Float amount;
}
