package IotSystem.IoTSystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class_assignments")
public class ClassAssignment implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "classAssignment_id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @MapsId("classId")  // map classId của embeddedId
    @JoinColumn(name = "class_id")
    private Classes clazz;

    @ManyToOne
    @MapsId("lecturerId")  // map lecturerId của embeddedId
    @JoinColumn(name = "lecturer_id")
    private Account lecturer;
}

