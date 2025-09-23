package IotSystem.IoTSystem.Model.Entities;


import IotSystem.IoTSystem.Model.Entities.Enum.Kit_Items_Types;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kits_items")
public class Kit_Items extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String Name;
    private Kit_Items_Types component_Type;

    private BigInteger quantity_total;
    private BigInteger quantity_available;

    private BigInteger price;

    private String description;


    private String imageUrl;


    private int quantity;
}
