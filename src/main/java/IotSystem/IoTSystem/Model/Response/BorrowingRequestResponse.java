package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;

import java.time.LocalDateTime;
import java.util.UUID;

public class BorrowingRequestResponse {
    private UUID id;
    private String qrCode;
    private String status;
    private String reason;
    private Double depositAmount;
    private LocalDateTime approvedDate;
    private LocalDateTime expectReturnDate;
    private LocalDateTime actualReturnDate;
    private Boolean isLate;
    private String note;
    private RequestType requestType;

    // Thông tin liên quan
    private String kitName;
    private String requestedByEmail;
    private String penaltyDescription;
}
