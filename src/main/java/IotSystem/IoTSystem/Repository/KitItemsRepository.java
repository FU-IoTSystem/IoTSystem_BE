package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kit_Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KitItemsRepository extends JpaRepository<Kit_Items, UUID> {


}
