package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
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
public class Kit_Component extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "Kit_Component_Id")
    private UUID id;

    private String componentName;
    private Kit_Items_Types componentType;
    private String description;

    private Integer quantityTotal;
    private Integer quantityAvailable;
    private Double pricePerCom;

    private String status;

    private String imageUrl;




    @ManyToOne
    @JoinColumn(name = "kit_id")
    private Kits kit;



}
