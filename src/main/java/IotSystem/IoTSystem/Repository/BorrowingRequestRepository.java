package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingRequestRepository extends JpaRepository<BorrowingRequest, UUID> {
    List<BorrowingRequest> findByRequestedById(UUID requestedById);

    List<BorrowingRequest> findByStatus(String status);

    List<BorrowingRequest> findByStatusIn(List<String> statuses);

    @Query("SELECT br FROM BorrowingRequest br WHERE br.kit.id = :kitId")
    List<BorrowingRequest> findByKitId(@Param("kitId") UUID kitId);

    // Projection for borrow/penalty aggregation
    interface BorrowPenaltyStats {
        Long getTotalBorrow();
        Double getTotalPenalty();
        java.time.LocalDate getStatDate();
    }

    @Query("""
        SELECT FUNCTION('date', br.createdAt) as statDate,
               COUNT(br.id) as totalBorrow,
               COALESCE(SUM(pd.amount), 0) as totalPenalty
        FROM BorrowingRequest br
        LEFT JOIN Penalty p ON p.request.id = br.id
        LEFT JOIN PenaltyDetail pd ON pd.penalty.id = p.id
        GROUP BY FUNCTION('date', br.createdAt)
    """)
    List<BorrowPenaltyStats> aggregateBorrowAndPenalty();
}
