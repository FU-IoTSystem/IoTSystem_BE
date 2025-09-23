package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Service.PenaltyPoliciesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PenaltyPoliciesServiceImpl implements PenaltyPoliciesService {
    @Override
    public List<PenaltyPolicies> getAll() {
        return List.of();
    }

    @Override
    public PenaltyPolicies getById(UUID id) {
        return null;
    }

    @Override
    public PenaltyPolicies create(PenaltyPolicies policy) {
        return null;
    }

    @Override
    public PenaltyPolicies update(UUID id, PenaltyPolicies policy) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
