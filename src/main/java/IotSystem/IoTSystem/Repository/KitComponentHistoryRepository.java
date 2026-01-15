package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.KitComponentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KitComponentHistoryRepository extends JpaRepository<KitComponentHistory, UUID> {
    List<KitComponentHistory> findByKitIdOrderByCreatedAtDesc(UUID kitId);
    List<KitComponentHistory> findByComponentIdOrderByCreatedAtDesc(UUID componentId);
    List<KitComponentHistory> findByKitIsNullOrderByCreatedAtDesc();
    List<KitComponentHistory> findAllByOrderByCreatedAtDesc();
}

