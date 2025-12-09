package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Mappers.PenaltyPoliciesMapper;
import IotSystem.IoTSystem.Model.Response.PenaltyPoliciesResponse;
import IotSystem.IoTSystem.Repository.PenaltyPoliciesRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Service.IPenaltyPoliciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PenaltyPoliciesServiceImpl implements IPenaltyPoliciesService {

    @Autowired
    private PenaltyPoliciesRepository penaltyPoliciesRepository;

    @Autowired
    private PenaltyRepository penaltyRepository;


    @Override
    public List<PenaltyPoliciesResponse> getAll() {
        List<PenaltyPolicies> policies = penaltyPoliciesRepository.findAll();
        return policies.stream().map(PenaltyPoliciesMapper::toResponse).toList();
    }

    @Override
    public PenaltyPolicies getById(UUID id) {
        return penaltyPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found PenaltyPolicy with ID: " + id));
    }

    @Override
    public PenaltyPolicies create(PenaltyPolicies policy) {
        // Handle penalty relationship if penaltyId is provided via penalty reference
        if (policy.getPenalty() != null && policy.getPenalty().getId() != null) {
            UUID penaltyId = policy.getPenalty().getId();
            Penalty penalty = penaltyRepository.findById(penaltyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Did not found Penalty with ID: " + penaltyId));
            policy.setPenalty(penalty);
        } else {
            // If penalty is null, set it to null explicitly
            policy.setPenalty(null);
        }

        // Save the penalty policy
        PenaltyPolicies savedPolicy = penaltyPoliciesRepository.save(policy);

        return savedPolicy;
    }

    @Override
    public PenaltyPolicies update(UUID id, PenaltyPolicies policy) {
        // Find existing policy
        PenaltyPolicies existingPolicy = penaltyPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found PenaltyPolicy with ID: " + id));

        // Update policy fields
        if (policy.getPolicyName() != null) {
            existingPolicy.setPolicyName(policy.getPolicyName());
        }
        if (policy.getType() != null) {
            existingPolicy.setType(policy.getType());
        }
        if (policy.getAmount() != null) {
            existingPolicy.setAmount(policy.getAmount());
        }
        if (policy.getIssuedDate() != null) {
            existingPolicy.setIssuedDate(policy.getIssuedDate());
        }
        if (policy.getResolved() != null) {
            existingPolicy.setResolved(policy.getResolved());
        }

        // Handle penalty relationship
        if (policy.getPenalty() != null && policy.getPenalty().getId() != null) {
            UUID penaltyId = policy.getPenalty().getId();
            Penalty penalty = penaltyRepository.findById(penaltyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Did not found Penalty with ID: " + penaltyId));
            existingPolicy.setPenalty(penalty);
        } else if (policy.getPenalty() == null) {
            // If penalty is explicitly set to null, remove the relationship
            existingPolicy.setPenalty(null);
        }

        // Save the updated policy
        PenaltyPolicies updatedPolicy = penaltyPoliciesRepository.save(existingPolicy);

        return updatedPolicy;
    }

    @Override
    public void delete(UUID id) {
        PenaltyPolicies policy = penaltyPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found PenaltyPolicy with ID: " + id));

        penaltyPoliciesRepository.deleteById(id);
    }
}
