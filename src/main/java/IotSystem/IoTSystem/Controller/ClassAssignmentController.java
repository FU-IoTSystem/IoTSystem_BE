package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.ClassAssignmentRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;
import IotSystem.IoTSystem.Model.Response.ClassResponse;
import IotSystem.IoTSystem.Service.IClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/class-assignments")
public class ClassAssignmentController {

    @Autowired
    @Qualifier("classAssignmentServiceImpl")
    private IClassAssignmentService classAssignmentService;

    @GetMapping
    public ResponseEntity<List<ClassAssignmentResponse>> getAll() {
        List<ClassAssignmentResponse> responses = classAssignmentService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassAssignmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(classAssignmentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassAssignmentResponse>> create(@RequestBody ClassAssignmentRequest request) {
        try {
            ClassAssignmentResponse response = classAssignmentService.create(request);

            ApiResponse<ClassAssignmentResponse> apiResponse = new ApiResponse<>();
            apiResponse.setStatus(HTTPStatus.Ok);
            apiResponse.setMessage("Class assignment created successfully");
            apiResponse.setData(response);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<ClassAssignmentResponse> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.BadRequest);
            errorResponse.setMessage("Failed to create class assignment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/unassigned-classes")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getUnassignedClasses() {
        try {
            List<ClassResponse> unassignedClasses = classAssignmentService.getUnassignedClasses();

            ApiResponse<List<ClassResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched unassigned classes successfully");
            response.setData(unassignedClasses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ClassResponse>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to fetch unassigned classes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassAssignmentResponse> update(@PathVariable UUID id, @RequestBody ClassAssignmentRequest request) {
        ClassAssignmentResponse response = classAssignmentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        try {
            classAssignmentService.delete(id);

            ApiResponse<Void> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Class assignment deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to delete class assignment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
