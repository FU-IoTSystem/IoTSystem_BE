package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PenaltyDetailRepository extends JpaRepository<PenaltyDetail, UUID> {
    
    @Query("SELECT pd FROM PenaltyDetail pd WHERE pd.penalty.id = :penaltyId")
    List<PenaltyDetail> findByPenaltyId(@Param("penaltyId") UUID penaltyId);
    
    @Query("SELECT pd FROM PenaltyDetail pd WHERE pd.policies.id = :policiesId")
    List<PenaltyDetail> findByPoliciesId(@Param("policiesId") UUID policiesId);
}

