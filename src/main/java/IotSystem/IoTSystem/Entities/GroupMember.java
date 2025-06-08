package IotSystem.IoTSystem.Entities;

import IotSystem.IoTSystem.Entities.Embedded.GroupMemberId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_members")
public class GroupMember {
    @EmbeddedId
    private GroupMemberId groupMemberId;

    private String role;

    @ManyToOne
    @MapsId("groupId")
    private StudentGroup group;

    @ManyToOne
    @MapsId("userId")
    private Account user;
}
