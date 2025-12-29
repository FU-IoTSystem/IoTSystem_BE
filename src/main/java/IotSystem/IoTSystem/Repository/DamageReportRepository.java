package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface DamageReportRepository extends JpaRepository<DamageReport, UUID> {

    // Delete damage reports by account ID using native query to avoid enum mapping issues
    @Modifying
    @Query(value = "DELETE FROM reports WHERE generated_by = :accountId", nativeQuery = true)
    void deleteByGeneratedByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT dr FROM DamageReport dr WHERE dr.kit.id = :kitId")
    List<DamageReport> findByKitId(@Param("kitId") UUID kitId);
}
