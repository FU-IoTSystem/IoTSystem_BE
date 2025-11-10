package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Request.PenaltyDetailRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyDetailResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class PenaltyDetailMapper {

    public PenaltyDetail toEntity(PenaltyDetailRequest request, PenaltyPolicies policies, Penalty penalty) {
        PenaltyDetail detail = new PenaltyDetail();
        detail.setAmount(request.getAmount());
        detail.setDescription(request.getDescription());

        // Handle createdAt conversion (Date -> LocalDateTime). If null, let service set current time.
        if (request.getCreatedAt() != null) {
            detail.setCreatedAt(request.getCreatedAt());
        }

        if (policies != null) {
            detail.setPolicies(policies);
        }
        if (penalty != null) {
            detail.setPenalty(penalty);
        }
        return detail;
    }

    public static PenaltyDetailResponse toResponse(PenaltyDetail entity) {
        PenaltyDetailResponse response = new PenaltyDetailResponse();
        response.setId(entity.getId());
        response.setAmount(entity.getAmount());
        response.setDescription(entity.getDescription());

        // Convert LocalDateTime -> Date for response consistency
        if (entity.getCreatedAt() != null) {
            response.setCreatedAt(entity.getCreatedAt());
        }

        if (entity.getPolicies() != null) {
            response.setPoliciesId(entity.getPolicies().getId());
        }
        if (entity.getPenalty() != null) {
            response.setPenaltyId(entity.getPenalty().getId());
        }
        return response;
    }

    public void updateEntity(PenaltyDetailRequest request, PenaltyDetail existing, PenaltyPolicies policies, Penalty penalty) {
        if (request.getAmount() != null) {
            existing.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getCreatedAt() != null) {
            existing.setCreatedAt(request.getCreatedAt());
        }
        if (policies != null) {
            existing.setPolicies(policies);
        }
        if (penalty != null) {
            existing.setPenalty(penalty);
        }
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static Date convertToDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}


