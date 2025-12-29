package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Mappers.NotificationMapper;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Response.NotificationResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.NotificationRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Service.INotificationService;
import IotSystem.IoTSystem.Service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public List<Notification> getAll() {
        return List.of();
    }

    @Override
    public List<NotificationResponse> getByRole() {
        Roles role = rolesRepository.findByName("ADMIN").orElseThrow(()
                -> new ResourceNotFoundException("Role not found"));
        List<Notification> notifications = notificationRepository.findByRoles(role);
        return notifications.stream().map(NotificationMapper::toResponse).toList();
    }


    @Override
    public List<NotificationResponse> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Notification> notifications = notificationRepository.findByUser(account);

        return notifications.stream().map(NotificationMapper::toResponse).toList();
    }

    @Override
    public void create(NotificationRequest request) {
        if (request.getSubType() == null) {
            throw new IllegalArgumentException("Notification subtype is required");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account actor = null;
        if (auth != null) {
            String email = auth.getName();
            actor = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Notification notification = new Notification();
        notification.setIsRead(false);
        notification.setSubType(request.getSubType());
        notification.setType(request.getSubType().getType());

        Account targetAccount = null;
        if (request.getUserId() != null) {
            targetAccount = accountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else if (actor != null) {
            targetAccount = actor;
        }

        notification.setUser(targetAccount);

        String defaultTitle;
        String defaultMessage;

        switch (request.getSubType()) {
            case BORROW_REQUEST_CREATED -> {
                Roles adminRole = rolesRepository.findByName("ADMIN")
                        .orElseThrow(() -> new ResourceNotFoundException("The role is not found"));
                notification.setRoles(adminRole);
                notification.setUser(null); // gửi theo vai trò ADMIN
                String actorName = actor != null ? actor.getFullName() : "Một người dùng";
                defaultTitle = "Yêu cầu mượn mới";
                defaultMessage = actorName + " đã tạo yêu cầu mượn Kit/Component.";
            }
            case RENTAL_FAILED_INSUFFICIENT_BALANCE -> {
                defaultTitle = "Số dư không đủ";
                defaultMessage = "Số dư ví không đủ, vui lòng nạp thêm để tiếp tục.";
            }
            case RENTAL_SUCCESS -> {
                defaultTitle = "Thuê kit thành công";
                defaultMessage = "Yêu cầu thuê kit của bạn đã được chấp nhận.";
            }
            case RENTAL_REQUEST -> {
                defaultTitle = "Đã gửi yêu cầu thuê kit";
                defaultMessage = "Yêu cầu thuê kit của bạn đã được gửi tới quản trị viên.";
            }
            case DEPOSIT_SUCCESS -> {
                defaultTitle = "Nạp tiền thành công";
                defaultMessage = "Bạn đã nạp tiền vào ví thành công.";
            }
            case DEPOSIT_FAILED -> {
                defaultTitle = "Nạp tiền thất bại";
                defaultMessage = "Giao dịch nạp tiền không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ.";
            }
            case OVERDUE_RETURN -> {
                defaultTitle = "Cảnh báo: Trễ hạn trả kit";
                defaultMessage = "Bạn có đơn mượn kit đã quá hạn. Vui lòng trả kit sớm để tránh phát sinh thêm phí phạt.";
            }
            case UNPAID_PENALTY -> {
                defaultTitle = "Cảnh báo: Chưa đóng tiền phạt";
                defaultMessage = "Bạn còn khoản phạt chưa thanh toán. Vui lòng xử lý sớm để tránh ảnh hưởng tới quyền lợi.";
            }
            case SYSTEM_ANNOUNCEMENT -> {
                defaultTitle = "Thông báo hệ thống";
                defaultMessage = "Bạn có thông báo mới từ hệ thống.";
            }
            case RENTAL_REJECTED -> {
                defaultTitle = "Thông báo từ QTV";
                defaultMessage = "Yêu cầu của bạn đã bị từ chối.";
            }
            case LECTURER_DELETE_BLOCKED_BY_CLASS -> {
                defaultTitle = "Không thể xóa giảng viên";
                defaultMessage = "Không thể xóa giảng viên vì giảng viên này đang được gán vào lớp học. Vui lòng xóa hoặc thay đổi gán giảng viên cho các lớp này trước khi xóa giảng viên.";
            }
            default -> {
                defaultTitle = request.getSubType().name();
                defaultMessage = "";
            }
        }

        notification.setTitle(request.getTitle() != null ? request.getTitle() : defaultTitle);
        notification.setMessage(request.getMessage() != null ? request.getMessage() : defaultMessage);

        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponse notificationResponse = NotificationMapper.toResponse(savedNotification);

        // Send real-time notification via WebSocket
        try {
            if (notification.getRoles() != null && notification.getRoles().getName().equals("ADMIN")) {
                // Send to all admins
                webSocketService.sendNotificationToAdmins(notificationResponse);
            } else if (notification.getUser() != null && notification.getUser().getId() != null) {
                // Send to specific user
                webSocketService.sendNotificationToUser(
                        notification.getUser().getId().toString(),
                        notificationResponse
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending WebSocket notification: " + e.getMessage());
        }
    }

    @Override
    public Notification update(UUID id, Notification notification) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public NotificationResponse markAsRead(UUID id) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account currentUser = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Find notification
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Verify that the notification belongs to the current user
        if (notification.getUser() != null && !notification.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You can only mark your own notifications as read");
        }

        // Mark as read
        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return NotificationMapper.toResponse(updatedNotification);
    }
}
