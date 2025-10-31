package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private String message;
    private Boolean isRead;

    private UUID userId;
    private String userEmail; // show thêm email của user để tiện hiển thị
}
