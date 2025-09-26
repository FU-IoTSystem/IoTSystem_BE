package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
public class KitItemResponse { //Reponse cho staff/admin xử lý
    private UUID id;
    private String name;
    private Kit_Items_Types componentType;
    private BigInteger quantityTotal;
    private BigInteger quantityAvailable;
    private BigInteger price;
    private String description;
    private String imageUrl;
}
