package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StudentGroupRequest {
    private String groupName;
    private UUID classId;
    private UUID accountId;
    private boolean status;
    private GroupRoles roles;
}
