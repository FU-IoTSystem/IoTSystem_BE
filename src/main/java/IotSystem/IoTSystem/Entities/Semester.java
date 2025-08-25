package IotSystem.IoTSystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Semester {
    @Id
    Long Id;
    String Name;
    LocalDate startDate;
    LocalDate endDate;
}
