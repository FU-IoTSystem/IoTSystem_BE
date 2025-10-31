package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class KitItemRequest {
    private String name;
    private Kit_Items_Types componentType;
    private BigInteger quantityTotal;
    private BigInteger quantityAvailable;
    private BigInteger price;
    private String description;
    private String imageUrl;
    private int quantity; // số lượng mượn (nếu cần cho use case borrow)
}
