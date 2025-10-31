package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.RequestKitComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestKitComponentRepository extends JpaRepository<RequestKitComponent, UUID> {
    
    List<RequestKitComponent> findByRequestId(UUID requestId);
    
    @Query("SELECT rkc FROM RequestKitComponent rkc WHERE rkc.requestId = :requestId")
    List<RequestKitComponent> findComponentsByRequestId(@Param("requestId") UUID requestId);
    
    List<RequestKitComponent> findByKitComponentsId(UUID kitComponentsId);
}

