package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.NotificationResponse;
import IotSystem.IoTSystem.Service.INotificationService;
import com.beust.ah.A;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


    @Autowired
    private INotificationService notificationService;

    @GetMapping("/role")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByRole() {
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>();

        List<NotificationResponse> notifications = notificationService.getByRole();

        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetch notification successfully");
        response.setData(notifications);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser() {

        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>();

        List<NotificationResponse> notifications = notificationService.getByUser();

        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetch notification successfully");
        response.setData(notifications);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody NotificationRequest request) {
        notificationService.create(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/create-notifications")
    public ResponseEntity<?> createMultiple(@RequestBody List<NotificationRequest> requests){
        for(NotificationRequest request : requests){
            notificationService.create(request);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
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
