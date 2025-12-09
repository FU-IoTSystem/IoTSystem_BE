package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenaltyPoliciesRepository  extends JpaRepository<PenaltyPolicies, UUID> {
}
