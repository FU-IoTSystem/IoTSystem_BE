package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, UUID> {
}
