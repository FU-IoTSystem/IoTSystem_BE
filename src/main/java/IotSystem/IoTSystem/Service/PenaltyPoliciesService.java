package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;

import java.util.List;
import java.util.UUID;

public interface PenaltyPoliciesService {
    List<PenaltyPolicies> getAll();

    PenaltyPolicies getById(UUID id);

    PenaltyPolicies create(PenaltyPolicies policy);

    PenaltyPolicies update(UUID id, PenaltyPolicies policy);

    void delete(UUID id);
}
