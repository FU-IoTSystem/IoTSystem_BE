package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.MaintenanceIssue;
import IotSystem.IoTSystem.Model.Entities.MaintenancePlan;
import IotSystem.IoTSystem.Model.Mappers.MaintenanceMapper;
import IotSystem.IoTSystem.Model.Request.MaintenanceIssueRequest;
import IotSystem.IoTSystem.Model.Request.MaintenancePlanRequest;
import IotSystem.IoTSystem.Model.Response.MaintenanceIssueResponse;
import IotSystem.IoTSystem.Model.Response.MaintenancePlanResponse;
import IotSystem.IoTSystem.Repository.MaintenanceIssueRepository;
import IotSystem.IoTSystem.Repository.MaintenancePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MaintenanceService {

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private MaintenanceIssueRepository maintenanceIssueRepository;

    public MaintenancePlanResponse createMaintenancePlan(MaintenancePlanRequest request) {
        MaintenancePlan plan = MaintenanceMapper.toMaintenancePlanEntity(request);
        plan = maintenancePlanRepository.save(plan);
        webSocketService.sendSystemUpdate("MAINTENANCE", "CREATE");
        return MaintenanceMapper.toMaintenancePlanResponse(plan);
    }

    public List<MaintenancePlanResponse> getAllMaintenancePlans() {
        List<MaintenancePlan> plans = maintenancePlanRepository.findAll();
        return MaintenanceMapper.toMaintenancePlanResponseList(plans);
    }

    public MaintenancePlanResponse getMaintenancePlanById(UUID id) {
        MaintenancePlan plan = maintenancePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Plan not found with id: " + id));
        return MaintenanceMapper.toMaintenancePlanResponse(plan);
    }

    public MaintenancePlanResponse updateMaintenancePlan(UUID id, MaintenancePlanRequest request) {
        MaintenancePlan plan = maintenancePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Plan not found with id: " + id));

        plan.setScheduledDate(request.getScheduledDate());
        plan.setStatus(request.getStatus());

        if (request.getReason() != null) {
            plan.setReason(request.getReason());
        }

        plan = maintenancePlanRepository.save(plan);
        webSocketService.sendSystemUpdate("MAINTENANCE", "UPDATE");
        return MaintenanceMapper.toMaintenancePlanResponse(plan);
    }

    public void deleteMaintenancePlan(UUID id) {
        if (!maintenancePlanRepository.existsById(id)) {
            throw new RuntimeException("Maintenance Plan not found with id: " + id);
        }
        maintenancePlanRepository.deleteById(id);
        webSocketService.sendSystemUpdate("MAINTENANCE", "DELETE");
    }

    public MaintenanceIssueResponse createMaintenanceIssue(MaintenanceIssueRequest request) {
        MaintenancePlan plan = maintenancePlanRepository.findById(request.getMaintenancePlanId())
                .orElseThrow(() -> new RuntimeException("Maintenance Plan not found with id: " + request.getMaintenancePlanId()));

        MaintenanceIssue issue = MaintenanceMapper.toMaintenanceIssueEntity(request, plan);
        issue = maintenanceIssueRepository.save(issue);
        return MaintenanceMapper.toMaintenanceIssueResponse(issue);
    }

    public List<MaintenanceIssueResponse> getAllMaintenanceIssues() {
        List<MaintenanceIssue> issues = maintenanceIssueRepository.findAll();
        return MaintenanceMapper.toMaintenanceIssueResponseList(issues);
    }

    public List<MaintenanceIssueResponse> getIssuesByPlan(UUID planId) {
        List<MaintenanceIssue> issues = maintenanceIssueRepository.findByMaintenancePlan_Id(planId);
        return MaintenanceMapper.toMaintenanceIssueResponseList(issues);
    }
}
