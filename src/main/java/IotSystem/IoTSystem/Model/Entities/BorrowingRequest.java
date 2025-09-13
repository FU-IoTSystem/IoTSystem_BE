package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrowing_requests")
public class BorrowingRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "borrowing_id",  columnDefinition = "uuid")
    private UUID id;

    private Date borrowDate;
    private Date expectedReturnDate;
    private Date actualReturnDate;
    private String status;
    private String note;

    private Double penaltyAmount;
    private Double depositAmount;

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





}
