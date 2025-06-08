package IotSystem.IoTSystem.Entities;

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

    @ManyToOne
    @JoinColumn(name = "kit_id")
    private Kits kit;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private Account requestedBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudentGroup group;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Account approvedBy;

    private LocalDateTime approvedDate;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;
}

