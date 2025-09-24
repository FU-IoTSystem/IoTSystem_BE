package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


    @Autowired
    private INotificationService notificationService;

    @GetMapping("getAll")
    public List<Notification> getAll() {
        return notificationService.getAll();
    }

    @GetMapping("/getbyId/{id}")
    public Notification getById(@PathVariable UUID id) {
        return notificationService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getByUser(@PathVariable UUID userId) {
        return notificationService.getByUser(userId);
    }

    @PostMapping("/create")
    public Notification create(@RequestBody Notification notification) {
        return notificationService.create(notification);
    }

    @PutMapping("/update/{id}")
    public Notification update(@PathVariable UUID id, @RequestBody Notification notification) {
        return notificationService.update(id, notification);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        notificationService.delete(id);
    }
}
