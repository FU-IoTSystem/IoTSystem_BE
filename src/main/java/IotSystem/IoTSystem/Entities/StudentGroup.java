package IotSystem.IoTSystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_groups")
public class StudentGroup {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;
}
