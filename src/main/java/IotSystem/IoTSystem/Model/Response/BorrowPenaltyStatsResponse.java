package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowPenaltyStatsResponse {
    private Long totalBorrow;
    private Double totalPenalty;
    private LocalDate statDate;
}

