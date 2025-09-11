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
    @Column(name = "class_assignment_id",  columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes clazz;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

}

