package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StudentGroupResponse {
    private UUID id;
    private String groupName;
    private boolean status;
    private GroupRoles roles;

    private UUID classId;
    private String className;

    private UUID accountId;
    private String studentName;
    private String studentEmail;
}
