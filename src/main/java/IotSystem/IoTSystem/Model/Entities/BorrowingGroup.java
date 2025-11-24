package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "borrowing_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingGroup extends Base{
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private GroupRoles roles;

    @ManyToOne
    @JoinColumn(name="student_id_group")
    private StudentGroup studentGroup;

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
