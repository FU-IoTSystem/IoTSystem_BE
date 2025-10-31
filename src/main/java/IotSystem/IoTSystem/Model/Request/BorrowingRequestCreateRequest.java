package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;

import java.time.LocalDateTime;
import java.util.UUID;

public class BorrowingRequestCreateRequest {
    private UUID kitId;              // chọn kit nào để mượn
    private Double depositAmount;    // tiền cọc
    private LocalDateTime expectReturnDate; // ngày dự kiến trả
    private String reason;           // lý do mượn
    private RequestType requestType; // loại yêu cầu
}
