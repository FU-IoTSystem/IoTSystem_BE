package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BorrowingRequestCreateRequest {
    private UUID kitId;              // chọn kit nào để mượn
//    private Double depositAmount;    // tiền cọc
    private UUID accountID; // who rent?
    private LocalDateTime expectReturnDate; // ngày dự kiến trả
    private String reason;           // lý do mượn
    private RequestType requestType; // loại yêu cầu
}
