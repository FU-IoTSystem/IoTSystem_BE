package IotSystem.IoTSystem.Model.Response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
public class KitItemBorrowResponse { //for student and teacher use cases
    private UUID id;
    private String name;
    private String description;
    private BigInteger quantityAvailable;
    private String imageUrl;
}