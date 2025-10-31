package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Notification;
import IotSystem.IoTSystem.Model.Entities.Roles;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification, UUID> {
    List<Notification> findByUser(Account user);
    List<Notification> findByRoles(Roles role);
}
