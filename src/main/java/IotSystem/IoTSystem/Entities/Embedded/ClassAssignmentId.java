package IotSystem.IoTSystem.Entities.Embedded;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClassAssignmentId implements Serializable {
    private UUID classId;

    private UUID lecturerId;
}
