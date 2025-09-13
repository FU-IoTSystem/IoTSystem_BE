package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Penalty {
    @Id
    @GeneratedValue
    private UUID id;

    private String courseName;
    private Double depositAmount;
    private Double penaltyAmount;
    private Double penaltyPerDay;
    private String damagePenalty;
    private String lostPenalty;
    private String kitType;

    private Date effectiveDate;
    private Date expiryDate;

    @ManyToOne
    @JoinColumn(name = "borrow_request_id")
    private BorrowingRequest request;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)  // FK -> accounts.id
    private Account account;
}
