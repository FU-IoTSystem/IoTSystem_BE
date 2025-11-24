package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_groups")
public class StudentGroup extends Base {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String groupName;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes clazz;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private boolean status;

    @OneToMany(mappedBy = "studentGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowingGroup> borrowingGroup = new ArrayList<>();
}
