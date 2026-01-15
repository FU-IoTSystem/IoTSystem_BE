package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "kit_component_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitComponentHistory extends Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "kit_id", nullable = true)
    private Kits kit;

    @ManyToOne
    @JoinColumn(name = "component_id", nullable = false)
    private Kit_Component component;

    @Column(name = "action")
    private String action;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;



    @ManyToOne
    @JoinColumn(name = "penalty_detail_id")
    private PenaltyDetail penaltyDetail;
}

