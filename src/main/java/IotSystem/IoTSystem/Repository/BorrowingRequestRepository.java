package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Entities.Enum.BorrowingRequestStatus;
import IotSystem.IoTSystem.Entities.Kits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingRequestRepository extends JpaRepository<BorrowingRequest, UUID> {

    // Tìm các borrowings theo classId và status
    List<BorrowingRequest> findByGroup_Clazz_IdAndStatus(UUID  classId, BorrowingRequestStatus status);


}
