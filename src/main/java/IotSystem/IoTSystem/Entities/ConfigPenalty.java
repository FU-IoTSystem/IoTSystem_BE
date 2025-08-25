package IotSystem.IoTSystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ConfigPenalty {
    @Id
    Long id;

    String key; // e.g. “depositRate”, “penaltyPerDay”

    String value;  // e.g. “100000”, “5000”

    @ManyToOne
    Semester semester;
}
