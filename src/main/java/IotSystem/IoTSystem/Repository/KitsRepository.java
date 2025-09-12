package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Entities.Account;
import IotSystem.IoTSystem.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Entities.Enum.KitType;
import IotSystem.IoTSystem.Entities.Kits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface KitsRepository extends JpaRepository<Kits, UUID> {




    List<Kits> findByType(KitType type);

    // Tìm theo status
    List<Kits> findByStatus(KitStatus status);

    // Tìm theo cả type và status
    List<Kits> findByTypeAndStatus(KitType type, KitStatus status);

    // Tìm theo QR code
    Optional<Kits> findByQrCode(String qrCode);

    // Đếm theo type
    long countByType(KitType type);


}
