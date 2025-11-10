package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Request.PenaltyPoliciesRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyPoliciesResponse;
import org.springframework.stereotype.Component;

@Component
public class PenaltyPoliciesMapper {

    public PenaltyPolicies toEntity(PenaltyPoliciesRequest request, Penalty penalty) {
        PenaltyPolicies policy = new PenaltyPolicies();
        policy.setPolicyName(request.getPolicyName());
        policy.setType(request.getType());
        policy.setAmount(request.getAmount());
        policy.setIssuedDate(request.getIssuedDate());
        policy.setResolved(request.getResolved());
        policy.setPenalty(penalty);
        return policy;
    }

    public static PenaltyPoliciesResponse toResponse(PenaltyPolicies entity) {
        PenaltyPoliciesResponse response = new PenaltyPoliciesResponse();
        response.setId(entity.getId());
        response.setPolicyName(entity.getPolicyName());
        response.setType(entity.getType());
        response.setAmount(entity.getAmount());
        response.setIssuedDate(entity.getIssuedDate());
        response.setResolved(entity.getResolved());

        if (entity.getPenalty() != null) {
            response.setPenaltyId(entity.getPenalty().getId());
        }
        return response;
    }

    public void updateEntity(PenaltyPoliciesRequest request, PenaltyPolicies existingPolicy, Penalty penalty) {
        if (request.getPolicyName() != null) {
            existingPolicy.setPolicyName(request.getPolicyName());
        }
        if (request.getType() != null) {
            existingPolicy.setType(request.getType());
        }
        if (request.getAmount() != null) {
            existingPolicy.setAmount(request.getAmount());
        }
        if (request.getIssuedDate() != null) {
            existingPolicy.setIssuedDate(request.getIssuedDate());
        }
        if (request.getResolved() != null) {
            existingPolicy.setResolved(request.getResolved());
        }
        if (penalty != null) {
            existingPolicy.setPenalty(penalty);
        }
    }
}

