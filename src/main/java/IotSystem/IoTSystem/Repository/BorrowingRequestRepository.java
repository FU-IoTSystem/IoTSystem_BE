package IotSystem.IoTSystem.Repository;


import IotSystem.IoTSystem.Entities.BorrowingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingRequestRepository extends JpaRepository<BorrowingRequest, UUID> {


}
