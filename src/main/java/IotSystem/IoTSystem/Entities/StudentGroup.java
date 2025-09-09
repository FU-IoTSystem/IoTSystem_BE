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
    @JoinColumn(name = "class_id")
    private Classes clazz;


    @ManyToOne
    @JoinColumn(name = "created_by", nullable = true) //cho hệ thống tạo nhưng vẫn có thể tạo thủ công
    private Account createdBy;

    boolean status;
}
