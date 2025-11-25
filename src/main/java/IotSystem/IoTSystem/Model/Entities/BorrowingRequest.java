package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrowing_requests")
public class BorrowingRequest extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "return_at")
    private LocalDateTime returnAt;

    @Column(name = "is_late")
    private Boolean isLate;

    @Column(name = "actual_return_date")
    private LocalDateTime actualReturnDate;

    @Column(name = "expected_return_date")
    private LocalDateTime expectReturnDate;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    // ================== RELATIONSHIPS ==================
    @ManyToOne
    @JoinColumn(name = "kit_id")
    private Kits kit;

    @ManyToOne
    @JoinColumn(name = "requested_by", nullable = false)
    private Account requestedBy;


    @ManyToOne
    @JoinColumn(name = "penalties_id")
    private Penalty penalties;


}





