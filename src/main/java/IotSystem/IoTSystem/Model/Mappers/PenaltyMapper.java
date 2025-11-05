package IotSystem.IoTSystem.Model.Mappers;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Request.PenaltyRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyResponse;
import org.springframework.stereotype.Component;

@Component
public class PenaltyMapper {
    public Penalty toEntity(PenaltyRequest request, BorrowingRequest borrowRequest, Account account, PenaltyPolicies policy) {
        Penalty penalty = new Penalty();
        penalty.setSemester(request.getSemester());
        penalty.setTake_effect_date(request.getTakeEffectDate());
        penalty.setKit_type(request.getKitType());
        penalty.setResolved(request.isResolved());
        penalty.setNote(request.getNote());
        penalty.setTotal_ammount(request.getTotalAmount());
        penalty.setRequest(borrowRequest);
        penalty.setAccount(account);
        penalty.setPolicies(policy);
        return penalty;
    }

    public PenaltyResponse toResponse(Penalty entity) {
        PenaltyResponse response = new PenaltyResponse();
        response.setId(entity.getId());
        response.setSemester(entity.getSemester());
        response.setTakeEffectDate(entity.getTake_effect_date());
        response.setKitType(entity.getKit_type());
        response.setResolved(entity.isResolved());
        response.setNote(entity.getNote());
        response.setTotalAmount(entity.getTotal_ammount());

        if (entity.getRequest() != null) {
            response.setBorrowRequestId(entity.getRequest().getId());
        }
        if (entity.getAccount() != null) {
            response.setAccountId(entity.getAccount().getId());
            response.setAccountEmail(entity.getAccount().getEmail());
        }
        if (entity.getPolicies() != null) {
            response.setPolicyId(entity.getPolicies().getId());
            response.setPolicyName(entity.getPolicies().getPolicyName()); // cần có field policyName trong PenaltyPolicies
        }
        return response;
    }
}
