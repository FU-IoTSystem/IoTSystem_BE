package IotSystem.IoTSystem.Model.Response;

import java.util.UUID;

public class ClassResponse {
    private UUID id;
    private String classCode;
    private String semester;
    private boolean status;

    // Thông tin giảng viên phụ trách
    private UUID teacherId;
    private String teacherName;
    private String teacherEmail;
}
