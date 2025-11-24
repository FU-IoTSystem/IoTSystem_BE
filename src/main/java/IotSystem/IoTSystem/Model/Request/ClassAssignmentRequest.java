package IotSystem.IoTSystem.Model.Request;

import lombok.Data;

import java.util.UUID;


@Data
public class ClassAssignmentRequest {
    private UUID classId;   // id lớp
    private UUID accountId; // id tài khoản (giảng viên/sinh viên)
//    private Roles roleId;    // id role (ví dụ: TEACHER, STUDENT)
}
