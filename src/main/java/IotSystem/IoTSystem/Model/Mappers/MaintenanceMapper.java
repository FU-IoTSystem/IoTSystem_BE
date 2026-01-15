package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.MaintenanceIssue;
import IotSystem.IoTSystem.Model.Entities.MaintenancePlan;
import IotSystem.IoTSystem.Model.Request.MaintenanceIssueRequest;
import IotSystem.IoTSystem.Model.Request.MaintenancePlanRequest;
import IotSystem.IoTSystem.Model.Response.MaintenanceIssueResponse;
import IotSystem.IoTSystem.Model.Response.MaintenancePlanResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceMapper {

    public static MaintenancePlan toMaintenancePlanEntity(MaintenancePlanRequest request) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setScope(request.getScope());
        plan.setScheduledDate(request.getScheduledDate());
        plan.setStatus(request.getStatus());
        plan.setCreatedBy(request.getCreatedBy());
        if (request.getReason() != null) {
            plan.setReason(request.getReason());
        }
        return plan;
    }

    public static MaintenancePlanResponse toMaintenancePlanResponse(MaintenancePlan plan) {
        return MaintenancePlanResponse.builder()
                .id(plan.getId())
                .scope(plan.getScope())
                .scheduledDate(plan.getScheduledDate())
                .status(plan.getStatus())
                .createdBy(plan.getCreatedBy())
                .reason(plan.getReason())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    public static MaintenanceIssue toMaintenanceIssueEntity(MaintenanceIssueRequest request, MaintenancePlan plan) {
        MaintenanceIssue issue = new MaintenanceIssue();
        issue.setIssueType(request.getIssueType());
        issue.setMaintenancePlan(plan);
        issue.setComponentId(request.getComponentId());
        if (request.getQuantity() != null) {
            issue.setQuantity(request.getQuantity());
        }
        return issue;
    }

    public static MaintenanceIssueResponse toMaintenanceIssueResponse(MaintenanceIssue issue) {
        return MaintenanceIssueResponse.builder()
                .id(issue.getId())
                .issueType(issue.getIssueType())
                .maintenancePlanId(issue.getMaintenancePlan().getId())
                .componentId(issue.getComponentId())
                .quantity(issue.getQuantity())
                .build();
    }

    public static List<MaintenancePlanResponse> toMaintenancePlanResponseList(List<MaintenancePlan> plans) {
        return plans.stream()
                .map(MaintenanceMapper::toMaintenancePlanResponse)
                .collect(Collectors.toList());
    }

    public static List<MaintenanceIssueResponse> toMaintenanceIssueResponseList(List<MaintenanceIssue> issues) {
        return issues.stream()
                .map(MaintenanceMapper::toMaintenanceIssueResponse)
                .collect(Collectors.toList());
    }
}
