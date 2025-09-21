package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
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
