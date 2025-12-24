package IotSystem.IoTSystem.Model.Entities.Enum;

import lombok.Getter;

@Getter
public enum NotificationSubType {
    // Giao dịch ví
    DEPOSIT_SUCCESS(NotificationType.DEPOSIT),
    DEPOSIT_FAILED(NotificationType.DEPOSIT),
    TRANSFER_SENT(NotificationType.DEPOSIT),
    TRANSFER_RECEIVED(NotificationType.DEPOSIT),

    // Thuê kit/component
    RENTAL_SUCCESS(NotificationType.SYSTEM),
    RENTAL_FAILED_INSUFFICIENT_BALANCE(NotificationType.SYSTEM),
    RENTAL_REQUEST(NotificationType.SYSTEM),
    RENTAL_REJECTED(NotificationType.ALERT),

    // Cảnh báo/quá hạn
    OVERDUE_RETURN(NotificationType.ALERT),      // Trễ hạn trả kit
    UNPAID_PENALTY(NotificationType.ALERT),      // Chưa đóng tiền phạt

    // Các hành động hệ thống/khác
    BORROW_REQUEST_CREATED(NotificationType.USER),
    SYSTEM_ANNOUNCEMENT(NotificationType.SYSTEM),

    // Cảnh báo xóa giảng viên
    LECTURER_DELETE_BLOCKED_BY_CLASS(NotificationType.ALERT);  // Không thể xóa giảng viên vì đang được gán vào lớp

    private final NotificationType type;

    NotificationSubType(NotificationType type) {
        this.type = type;
    }

}

