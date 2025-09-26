package IotSystem.IoTSystem.Model.Entities;

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
public class Notification {
    @Id
    @GeneratedValue
    private UUID id;

    private NotificationType type;
    private String message;
    private Boolean isRead;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user;
}
