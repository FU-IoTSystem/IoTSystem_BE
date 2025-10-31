package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PenaltyRequest {
    private String semester;
    private Date takeEffectDate;
    private KitType kitType;
    private boolean resolved;
    private String note;
    private BigInteger totalAmount;

    private UUID borrowRequestId; // FK -> BorrowingRequest
    private UUID accountId;       // FK -> Account
    private UUID policyId;        // FK -> PenaltyPolicies
}
