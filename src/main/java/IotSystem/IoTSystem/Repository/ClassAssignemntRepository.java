package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassAssignemntRepository extends JpaRepository<ClassAssignment, UUID> {
//    List<ClassAssignment> findByRoles(Roles roles);
    Optional<ClassAssignment> findByClazzAndAccount(Classes clazz, Account account);
    
    // Find all class assignments by class
    List<ClassAssignment> findByClazz(Classes clazz);
}
