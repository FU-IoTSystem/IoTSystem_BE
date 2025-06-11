package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Enum.BorrowingRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrowing_requests")
public class BorrowingRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id", nullable = false)
    private Kits kit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private Account requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudentGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Account approvedBy;

    private LocalDateTime approvedDate;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowingRequestStatus status;

    public void approve(Account approver) {
        this.approvedBy = approver;
        this.approvedDate = LocalDateTime.now();
        this.status = BorrowingRequestStatus.APPROVED;
    }

    public void reject(Account approver) {
        this.approvedBy = approver;
        this.approvedDate = LocalDateTime.now();
        this.status = BorrowingRequestStatus.REJECTED;
    }

    public void markAsReturned() {
        this.returnDate = LocalDateTime.now();
        this.status = BorrowingRequestStatus.RETURNED;
    }

    public void cancel() {
        this.status = BorrowingRequestStatus.CANCELED;
    }

}
