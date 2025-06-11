package IotSystem.IoTSystem.DTOs;

import IotSystem.IoTSystem.Entities.Enum.KitStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    private KitStatus status;
}

