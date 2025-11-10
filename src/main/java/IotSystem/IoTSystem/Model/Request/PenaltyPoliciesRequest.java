package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyPoliciesRequest {
    private String policyName;
    private String type;
    private Double amount;
    private Date issuedDate;
    private Date resolved;
    private UUID penaltyId;  // FK -> Penalty
}

