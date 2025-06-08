package IotSystem.IoTSystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
public class Kits {
    @Id
    private Integer id;

    private String type;
    private String status;
    private String qrCode;
    private String description;
}

