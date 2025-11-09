package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentRentalRequest {
    private UUID kitComponentsId;
    private String componentName;
    private Integer quantity;
    private String reason;
    private Double depositAmount;
    private LocalDateTime expectReturnDate;
}