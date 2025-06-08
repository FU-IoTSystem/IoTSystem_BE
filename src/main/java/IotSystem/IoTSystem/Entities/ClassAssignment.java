package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Embedded.ClassAssignmentId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class_assignments")
public class ClassAssignment implements Serializable {
    @EmbeddedId
    private ClassAssignmentId id;

    @ManyToOne
    @MapsId("classId")  // map classId của embeddedId
    @JoinColumn(name = "class_id")
    private Classes clazz;

    @ManyToOne
    @MapsId("lecturerId")  // map lecturerId của embeddedId
    @JoinColumn(name = "lecturer_id")
    private Account lecturer;
}

