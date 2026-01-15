package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PenaltyResponse {
    private UUID id;
    private String semester;
    private Date takeEffectDate;
    private KitType kitType;
    private boolean resolved;
    private String note;
    private BigInteger totalAmount;

    // Thông tin liên quan
    private UUID borrowRequestId;
    private UUID accountId;
    private String accountEmail;
    private UUID policyId;
    private String policyName;
    private String userCode;
}
