package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassAssignemntRepository extends JpaRepository<ClassAssignment, UUID> {
//    List<ClassAssignment> findByRoles(Roles roles);
}
