package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Roles;

import java.util.UUID;

public class ClassAssignmentRequest {
    private UUID classId;   // id lớp
    private UUID accountId; // id tài khoản (giảng viên/sinh viên)
    private Roles roleId;    // id role (ví dụ: TEACHER, STUDENT)
}
