package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingGroupResponse {
    private UUID id;
    private GroupRoles roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Student Group details
    private UUID studentGroupId;
    private String studentGroupName;
    private UUID classId;
    private String className;

    // Account details
    private UUID accountId;
    private String accountName;
    private String accountEmail;
    private String accountPhone;
    private String studentCode;

    // Active status
    private Boolean isActive;
}
