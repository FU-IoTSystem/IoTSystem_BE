package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Penalty extends Base {
    @Id
    @GeneratedValue
    private UUID id;

    private String semester;
    private Date take_effect_date;

    private KitType kit_type;
    private boolean resolved;

    private String note;
    private BigInteger total_ammount;
    @ManyToOne
    @JoinColumn(name = "borrow_request_id")
    private BorrowingRequest request;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)  // FK -> accounts.id
    private Account account;

    @ManyToOne
    @JoinColumn(name = "penalty_policies_id")
    private PenaltyPolicies policies;
}
