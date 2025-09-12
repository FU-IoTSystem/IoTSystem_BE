package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Entities.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface PenaltyRepository  extends JpaRepository<Penalty, UUID> {
}
