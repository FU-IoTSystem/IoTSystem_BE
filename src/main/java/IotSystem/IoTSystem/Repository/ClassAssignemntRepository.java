package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Entities.ClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository

public interface ClassAssignemntRepository extends JpaRepository<ClassAssignment, UUID> {


}
