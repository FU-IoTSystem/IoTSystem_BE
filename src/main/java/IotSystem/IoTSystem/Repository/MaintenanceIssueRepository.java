package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.MaintenanceIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintenanceIssueRepository extends JpaRepository<MaintenanceIssue, UUID> {
    java.util.List<MaintenanceIssue> findByMaintenancePlan_Id(UUID planId);
}
