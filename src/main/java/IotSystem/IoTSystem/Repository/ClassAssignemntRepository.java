package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Roles;
import org.springframework.data.jpa.repository.Query;
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

    // Find all class assignments where account has STUDENT role (for export)
    @Query("SELECT ca FROM ClassAssignment ca WHERE ca.account.role.name = 'STUDENT'")
    List<ClassAssignment> findAllStudentAssignments();

    // Find all class assignments by class where account has STUDENT role
    @Query("SELECT ca FROM ClassAssignment ca WHERE ca.clazz = :clazz AND ca.account.role.name = 'STUDENT'")
    List<ClassAssignment> findStudentAssignmentsByClass(Classes clazz);

    // Find all class assignments by account
    List<ClassAssignment> findByAccount(Account account);

    // Find all class assignments by account with STUDENT role
    @Query("SELECT ca FROM ClassAssignment ca WHERE ca.account = :account AND ca.account.role.name = 'STUDENT'")
    List<ClassAssignment> findStudentAssignmentsByAccount(Account account);
}
