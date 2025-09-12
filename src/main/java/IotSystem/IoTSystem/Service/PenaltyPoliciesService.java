package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Repository.PenaltyPoliciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class PenaltyPoliciesService {

    @Autowired
    private PenaltyPoliciesRepository repository;

    public List<PenaltyPolicies> getAll() {
        return repository.findAll();
    }

    public PenaltyPolicies getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public PenaltyPolicies create(PenaltyPolicies policy) {
        policy.setIssuedDate(new Date());
        return repository.save(policy);
    }

    public PenaltyPolicies update(UUID id, PenaltyPolicies updated) {
        PenaltyPolicies existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setType(updated.getType());
            existing.setAmount(updated.getAmount());
            existing.setIssuedDate(updated.getIssuedDate());
            existing.setResolved(updated.getResolved());
            existing.setRequest(updated.getRequest());
            existing.setPenalty(updated.getPenalty());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
