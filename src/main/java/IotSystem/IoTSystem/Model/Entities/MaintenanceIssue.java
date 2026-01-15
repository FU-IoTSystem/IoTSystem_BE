package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.MaintenanceIssueType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "maintenance_issues")
public class MaintenanceIssue extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_type")
    private MaintenanceIssueType issueType;

    @Column(name = "component_id")
    private UUID componentId;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_plan_id")
    private MaintenancePlan maintenancePlan;
}
