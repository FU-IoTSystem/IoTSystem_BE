package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import IotSystem.IoTSystem.Model.Entities.Enum.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationRequest {
    private NotificationType type;   // loại thông báo (SYSTEM, BORROWING, PENALTY...)
    private NotificationSubType subType;          // phân loại chi tiết (DEPOSIT_SUCCESS, RENTAL_FAILED_INSUFFICIENT_BALANCE,...)
    private String title;            // tiêu đề thông báo
    private String message;          // nội dung thông báo
    private UUID userId;             // id của account nhận thông báo
}
