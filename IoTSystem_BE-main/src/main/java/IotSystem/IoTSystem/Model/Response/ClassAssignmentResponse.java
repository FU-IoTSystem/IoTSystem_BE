package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassAssignmentResponse {
    private UUID id;
    private UUID classId;

    private UUID accountId;
    private String accountName;
    private String accountEmail;

    private Roles roleId;
    private String roleName;
    private java.time.LocalDateTime createdAt;
}
