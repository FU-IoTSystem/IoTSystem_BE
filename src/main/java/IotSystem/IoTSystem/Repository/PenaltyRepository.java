package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {

    @Query("SELECT p FROM Penalty p WHERE p.account.id = :accountId ORDER BY p.createdAt DESC")
    List<Penalty> findPenaltiesByAccountId(@Param("accountId") UUID accountId);

    List<Penalty> findByResolved(boolean isResolved);

    @Query("SELECT p FROM Penalty p WHERE p.request.id = :requestId")
    Penalty findByRequestId(@Param("requestId") UUID requestId);
}
