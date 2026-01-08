package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, UUID> {
}
