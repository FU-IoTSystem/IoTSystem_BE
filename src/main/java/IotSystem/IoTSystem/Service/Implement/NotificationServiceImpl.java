package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public List<Notification> getAll() {
        return List.of();
    }

    @Override
    public Notification getById(UUID id) {
        return null;
    }

    @Override
    public List<Notification> getByUser(UUID userId) {
        return List.of();
    }

    @Override
    public Notification create(Notification notification) {
        return null;
    }

    @Override
    public Notification update(UUID id, Notification notification) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
