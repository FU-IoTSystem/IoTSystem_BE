package IotSystem.IoTSystem.Model.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KitComponentBorrowResponse {
    private UUID id;
    private String componentName;
    private String description;
    private Integer quantityAvailable;
    private String imageUrl;
}
