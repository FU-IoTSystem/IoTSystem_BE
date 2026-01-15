package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.MaintenanceIssueRequest;
import IotSystem.IoTSystem.Model.Request.MaintenancePlanRequest;
import IotSystem.IoTSystem.Model.Response.MaintenanceIssueResponse;
import IotSystem.IoTSystem.Model.Response.MaintenancePlanResponse;
import IotSystem.IoTSystem.Service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*") // Allow requests from frontend
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @PostMapping("/plans")
    public ResponseEntity<MaintenancePlanResponse> createPlan(@RequestBody MaintenancePlanRequest request) {
        return ResponseEntity.ok(maintenanceService.createMaintenancePlan(request));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<MaintenancePlanResponse>> getAllPlans() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenancePlans());
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<MaintenancePlanResponse> getPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(maintenanceService.getMaintenancePlanById(id));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<MaintenancePlanResponse> updatePlan(@PathVariable UUID id, @RequestBody MaintenancePlanRequest request) {
        return ResponseEntity.ok(maintenanceService.updateMaintenancePlan(id, request));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        maintenanceService.deleteMaintenancePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/issues")
    public ResponseEntity<MaintenanceIssueResponse> createIssue(@RequestBody MaintenanceIssueRequest request) {
        return ResponseEntity.ok(maintenanceService.createMaintenanceIssue(request));
    }

    @GetMapping("/issues")
    public ResponseEntity<List<MaintenanceIssueResponse>> getAllIssues() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenanceIssues());
    }

    @GetMapping("/issues/plan/{planId}")
    public ResponseEntity<List<MaintenanceIssueResponse>> getIssuesByPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok(maintenanceService.getIssuesByPlan(planId));
    }
}
