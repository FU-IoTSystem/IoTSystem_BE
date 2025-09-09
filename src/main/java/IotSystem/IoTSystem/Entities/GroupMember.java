package IotSystem.IoTSystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "group_id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    private String role;


    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Account user;
}
