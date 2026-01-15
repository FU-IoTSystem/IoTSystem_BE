package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceScope;
import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "maintenance_plans")
public class MaintenancePlan extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MaintenanceScope scope;



    @Column(name = "scheduled_date")
    private Date scheduledDate;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "reason")
    private String reason;
}
