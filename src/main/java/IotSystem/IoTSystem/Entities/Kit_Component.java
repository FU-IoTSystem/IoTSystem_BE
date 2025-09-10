package IotSystem.IoTSystem.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "kit_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kit_Component {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID kitId; // giả sử có bảng kits

    private String componentName;
    private String componentType;

    @Column(length = 1000)
    private String description;

    private String unit;

    private Integer quantityTotal;
    private Integer quantityAvailable;

    private BigDecimal unitPrice;
    private BigDecimal totalValue;

    private String linkReference;
    private String status;
    private String location;

    private LocalDateTime lastCheckedDate;
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
