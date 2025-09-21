package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Model.Entities.Enum.KitStatus;
import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import IotSystem.IoTSystem.Model.Entities.Kits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitsRepository extends JpaRepository<Kits, Integer> {



    Optional<Kits> findById(Integer id);
    // Tìm theo type
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
