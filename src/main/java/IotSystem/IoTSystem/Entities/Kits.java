package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Entities.Enum.KitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kits")
public class Kits  {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String kitName;
    private String type;
    private String status;
    private String qrCode;
    private String description;
    private String location;
    private String refNumber;
    private String imageUrl;

    private Date lastMaintenanceDate;
    private Date createdAt;
    private Date updatedAt;



}

