package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingRequestRepository extends JpaRepository<BorrowingRequest, UUID> {
    List<BorrowingRequest> findByRequestedById(UUID requestedById);

    List<BorrowingRequest> findByStatus(String status);

    List<BorrowingRequest> findByStatusIn(List<String> statuses);
}
