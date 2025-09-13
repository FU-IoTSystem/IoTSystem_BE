package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Data;

import java.util.UUID;


@Data
public class KitResponseDTO {
    private UUID id;
    private KitType type;
    private KitStatus status;
    private String qrCode;
    private String description;
}
