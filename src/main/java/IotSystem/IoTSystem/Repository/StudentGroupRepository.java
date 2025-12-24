package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Entities.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGroupRepository  extends JpaRepository<StudentGroup, UUID> {
    // Find all student groups by class
    List<StudentGroup> findByClazz(Classes clazz);
}
