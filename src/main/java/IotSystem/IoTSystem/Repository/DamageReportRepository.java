package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Damge_Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface DamageReportRepository extends JpaRepository<Damge_Report, UUID> {
}
