package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Entities.Kit_Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KitComponentRepository extends JpaRepository<Kit_Component, UUID> {
}
