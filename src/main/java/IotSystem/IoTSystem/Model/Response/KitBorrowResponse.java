package IotSystem.IoTSystem.Model.Response;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KitBorrowResponse {
    private UUID id;
    private String kitName;
    private String description;
    private String imageUrl;
    private Integer quantityAvailable; // số lượng còn có thể mượn
}