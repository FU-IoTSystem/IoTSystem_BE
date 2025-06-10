package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Entities.Enum.KitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kits")
public class Kits extends Base {
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private KitType type;

    @Enumerated(EnumType.STRING)
    private KitStatus status;

    @Column(unique = true, nullable = false)  // QR code phải unique
    private String qrCode;
    private String description;
}

