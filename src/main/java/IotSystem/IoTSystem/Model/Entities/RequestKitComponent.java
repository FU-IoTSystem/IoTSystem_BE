package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "request_kit_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestKitComponent extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "kit_components_id")
    private UUID kitComponentsId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "component_name")
    private String componentName;

    // Many-to-One relationship with BorrowingRequest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", insertable = false, updatable = false)
    private BorrowingRequest borrowingRequest;

    // Many-to-One relationship with Kit_Component
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_components_id", insertable = false, updatable = false)
    private Kit_Component kitComponent;
}