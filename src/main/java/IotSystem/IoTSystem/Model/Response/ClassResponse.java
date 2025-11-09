package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    private UUID id;
    private String classCode;
    private String semester;
    private boolean status;

    // Thông tin giảng viên phụ trách
    private UUID teacherId;
    private String teacherName;
    private String teacherEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
