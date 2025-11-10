package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import IotSystem.IoTSystem.Model.Entities.Kits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KitsRepository extends JpaRepository<Kits, UUID> {



    Optional<Kits> findById(UUID id);


    List<Kits> findByType(KitType studentKit);
}
