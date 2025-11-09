package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Response.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request, Account user) {
        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false); // mặc định chưa đọc
        notification.setUser(user);
        return notification;
    }

    public NotificationResponse toResponse(Notification entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setMessage(entity.getMessage());
        response.setIsRead(entity.getIsRead());
        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
            response.setUserEmail(entity.getUser().getEmail());
        }
        return response;
    }
}
