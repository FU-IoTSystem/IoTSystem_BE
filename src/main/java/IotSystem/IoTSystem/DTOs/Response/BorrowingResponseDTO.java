package IotSystem.IoTSystem.DTOs.Response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponseDTO {
    private UUID id;
    private UUID kitId;
    private String kitType;
    private String kitStatus;
    private String requestedBy;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;
}
