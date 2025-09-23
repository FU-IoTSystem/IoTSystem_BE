package IotSystem.IoTSystem.Model.Entities;


import IotSystem.IoTSystem.Model.Entities.Enum.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Damage_Report")
public class Damge_Report extends Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "Report_id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    private String description;

    private ReportStatus reportStatus;
}
