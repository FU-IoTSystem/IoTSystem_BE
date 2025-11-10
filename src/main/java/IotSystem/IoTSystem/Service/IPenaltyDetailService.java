package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Request.PenaltyDetailRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyDetailResponse;

import java.util.List;
import java.util.UUID;

public interface IPenaltyDetailService {
    List<PenaltyDetail> getAll();

    PenaltyDetail getById(UUID id);

    PenaltyDetail create(PenaltyDetailRequest request);

    List<PenaltyDetail> createMultiple(List<PenaltyDetailRequest> requests);

    PenaltyDetail update(UUID id, PenaltyDetailRequest request);

    void delete(UUID id);

    List<PenaltyDetailResponse> findByPenaltyId(UUID penaltyId);

    List<PenaltyDetail> findByPoliciesId(UUID policiesId);
}

