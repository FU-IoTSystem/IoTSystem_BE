package IotSystem.IoTSystem.Model.Response;

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
public class PenaltyPoliciesResponse {
    private UUID id;
    private String policyName;
    private String type;
    private Double amount;
    private Date issuedDate;
    private Date resolved;
    
    // Thông tin liên quan
    private UUID penaltyId;
}

