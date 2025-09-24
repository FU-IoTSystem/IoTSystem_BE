package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Notification;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification, UUID> {
}
