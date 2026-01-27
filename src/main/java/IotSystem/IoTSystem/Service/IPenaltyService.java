package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Request.PenaltyRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyResponse;

import java.util.List;
import java.util.UUID;

public interface IPenaltyService {
    List<PenaltyResponse> getPenaltyByAccount();

    Penalty getById(UUID id);

    List<PenaltyResponse> getAll(boolean isResolved);

    List<PenaltyResponse> getAll();

    PenaltyResponse create(PenaltyRequest request);

    Penalty update(UUID id, Penalty penalty);

    void delete(UUID id);

    void confirmPaymentForPenalty(UUID penaltyId);

    PenaltyResponse getPenaltyByRequestId(UUID requestId);
}
