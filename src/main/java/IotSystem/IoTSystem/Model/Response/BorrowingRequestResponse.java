package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequestResponse {
    private UUID id;
    private String qrCode;
    private String status;
    private String reason;
    private Double depositAmount;
    private LocalDateTime createdAt;
    private LocalDateTime approvedDate;
    private LocalDateTime expectReturnDate;
    private LocalDateTime actualReturnDate;
    private Boolean isLate;
    private String note;
    private RequestType requestType;

    // Thông tin liên quan
    private KitResponse kit;
    private AccountInfo requestedBy;
//    private String penaltyDescription;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountInfo {
        private UUID id;
        private String email;
        private String fullName;
        private String phone;
    }
}
