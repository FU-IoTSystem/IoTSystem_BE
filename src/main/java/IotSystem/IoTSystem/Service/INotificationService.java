package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface INotificationService {
    List<Notification> getAll();

    List<NotificationResponse> getByRole();

    List<NotificationResponse> getByUser();

    void create(NotificationRequest request);

    Notification update(UUID id, Notification notification);

    void delete(UUID id);
}
