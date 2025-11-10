package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Response.PenaltyPoliciesResponse;

import java.util.List;
import java.util.UUID;

public interface IPenaltyPoliciesService {
    List<PenaltyPoliciesResponse> getAll();

    PenaltyPolicies getById(UUID id);

    PenaltyPolicies create(PenaltyPolicies policy);

    PenaltyPolicies update(UUID id, PenaltyPolicies policy);

    void delete(UUID id);
}
