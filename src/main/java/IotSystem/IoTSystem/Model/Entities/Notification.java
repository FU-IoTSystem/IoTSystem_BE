package IotSystem.IoTSystem.Model.Entities;

import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import IotSystem.IoTSystem.Model.Entities.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationSubType subType;
    private String title;
    private String message;
    private Boolean isRead;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles roles;
}
