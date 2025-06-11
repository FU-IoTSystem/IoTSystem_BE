package IotSystem.IoTSystem.DTOs;

import IotSystem.IoTSystem.Entities.Enum.KitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTypeRequest {
    private KitType type;

}
