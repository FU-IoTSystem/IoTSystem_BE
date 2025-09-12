package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.Notification;
import IotSystem.IoTSystem.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Notification getById(UUID id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getByUser(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification create(Notification notification) {
        notification.setCreatedAt(new Date());
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    public Notification update(UUID id, Notification updated) {
        Notification existing = notificationRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setType(updated.getType());
            existing.setMessage(updated.getMessage());
            existing.setIsRead(updated.getIsRead());
            existing.setUser(updated.getUser());
            return notificationRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        notificationRepository.deleteById(id);
    }
}
