package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<Notification> getAll();

    Notification getById(UUID id);

    List<Notification> getByUser(UUID userId);

    Notification create(Notification notification);

    Notification update(UUID id, Notification notification);

    void delete(UUID id);
}
