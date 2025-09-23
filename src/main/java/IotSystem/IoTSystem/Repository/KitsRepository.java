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


}
