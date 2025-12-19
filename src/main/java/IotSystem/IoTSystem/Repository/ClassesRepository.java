package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, UUID> {
    Optional<Classes> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);

    // Check if classCode exists for another class (excluding current class)
    @Query("SELECT COUNT(c) > 0 FROM Classes c WHERE c.classCode = :classCode AND c.id <> :excludeId")
    boolean existsByClassCodeExcludingId(@Param("classCode") String classCode, @Param("excludeId") UUID excludeId);
}
