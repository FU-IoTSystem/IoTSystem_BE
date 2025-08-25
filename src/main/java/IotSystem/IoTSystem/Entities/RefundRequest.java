package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Enum.EnumStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class RefundRequest {

    @Id
    Long id;

    @ManyToOne
    WalletTransaction walletTransaction;

    BigDecimal amount;
    EnumStatus status;    // PENDING / APPROVED / REJECTED
    LocalDateTime requestedAt, processedAt;
    @ManyToOne Account processedBy;
}
