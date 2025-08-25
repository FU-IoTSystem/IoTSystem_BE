package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Enum.DamageReportStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class DamageReport {
    @Id
    Long id;

    @ManyToOne
     BorrowingRequest request;

    String description;
    String photoURL;
    BigDecimal penaltyDamage;
    DamageReportStatus status;
    LocalDateTime reportedAt, processedAt;

    @ManyToOne
    Account processedBy;

}
