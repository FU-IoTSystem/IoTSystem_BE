package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Roles;

import java.util.UUID;

public class ClassAssignmentResponse {
    private UUID id;
    private UUID classId;
    private String className;

    private UUID accountId;
    private String accountName;
    private String accountEmail;

    private Roles roleId;
    private String roleName;
}
