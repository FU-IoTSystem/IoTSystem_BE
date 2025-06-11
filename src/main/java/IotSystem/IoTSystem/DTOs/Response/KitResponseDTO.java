package IotSystem.IoTSystem.DTOs.Response;

import IotSystem.IoTSystem.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Entities.Enum.KitType;
import lombok.Data;


@Data
public class KitResponseDTO {
    private Integer id;
    private KitType type;
    private KitStatus status;
    private String qrCode;
    private String description;
}
