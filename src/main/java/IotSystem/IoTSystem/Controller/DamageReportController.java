package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.DamageReport;
import IotSystem.IoTSystem.Model.Request.DamageReportRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Service.IDamageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/damage")
public class DamageReportController {

    @Autowired
    private IDamageReportService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DamageReport>>> getAll() {
        List<DamageReport> reports = service.getAll();
        ApiResponse<List<DamageReport>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched damage reports successfully");
        response.setData(reports);
        response.setTotal(reports.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageReport>> getById(@PathVariable UUID id) {
        DamageReport report = service.getById(id);
        ApiResponse<DamageReport> response = new ApiResponse<>();
        if (report != null) {
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched damage report successfully");
            response.setData(report);
            return ResponseEntity.ok(response);
        } else {
            response.setStatus(HTTPStatus.NotFound);
            response.setMessage("Damage report not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DamageReport>> create(@RequestBody DamageReportRequest request) {
        DamageReport report = service.create(request);
        ApiResponse<DamageReport> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Created damage report successfully");
        response.setData(report);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageReport>> update(@PathVariable UUID id, @RequestBody DamageReportRequest request) {
        DamageReport report = service.update(id, request);
        ApiResponse<DamageReport> response = new ApiResponse<>();
        if (report != null) {
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Updated damage report successfully");
            response.setData(report);
            return ResponseEntity.ok(response);
        } else {
            response.setStatus(HTTPStatus.NotFound);
            response.setMessage("Damage report not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Deleted damage report successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<DamageReport>>> getByAccountId(@PathVariable UUID accountId) {
        List<DamageReport> reports = service.getByAccountId(accountId);
        ApiResponse<List<DamageReport>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched damage reports by account successfully");
        response.setData(reports);
        response.setTotal(reports.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kit/{kitId}")
    public ResponseEntity<ApiResponse<List<DamageReport>>> getByKitId(@PathVariable UUID kitId) {
        List<DamageReport> reports = service.getByKitId(kitId);
        ApiResponse<List<DamageReport>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched damage reports by kit successfully");
        response.setData(reports);
        response.setTotal(reports.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrow-request/{borrowRequestId}")
    public ResponseEntity<ApiResponse<List<DamageReport>>> getByBorrowRequestId(@PathVariable UUID borrowRequestId) {
        List<DamageReport> reports = service.getByBorrowRequestId(borrowRequestId);
        ApiResponse<List<DamageReport>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched damage reports by borrow request successfully");
        response.setData(reports);
        response.setTotal(reports.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DamageReport>>> getByStatus(@PathVariable String status) {
        List<DamageReport> reports = service.getByStatus(status);
        ApiResponse<List<DamageReport>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched damage reports by status successfully");
        response.setData(reports);
        response.setTotal(reports.size());
        return ResponseEntity.ok(response);
    }
}

