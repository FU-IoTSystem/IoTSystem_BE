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
public class RequestKitComponent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private BorrowingRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_component_id", nullable = false)
    private Kit_Component kitComponent;

    private Integer quantity; // số lượng mượn
    private String note;      // ghi chú nếu cần
}
