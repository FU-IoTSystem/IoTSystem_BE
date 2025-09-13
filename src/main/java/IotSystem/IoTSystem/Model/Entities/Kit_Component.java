package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String componentName;
    private String componentType;
    private String description;
    private String unit;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Double unitPrice;
    private Double totalValue;
    private String status;
    private String location;
    private String imageUrl;

    private Date lastCheckedDate;
    private Date createdAt;
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "kit_id")
    private Kits kit;



}
