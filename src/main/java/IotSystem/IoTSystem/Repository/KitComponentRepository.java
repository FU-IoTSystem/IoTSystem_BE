package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KitComponentRepository extends JpaRepository<Kit_Component, UUID> {

    List<Kit_Component> findByKitId(UUID kitId);

    List<Kit_Component> findByKitIsNull();
}
