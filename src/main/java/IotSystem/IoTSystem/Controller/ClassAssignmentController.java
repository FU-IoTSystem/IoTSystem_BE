package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Request.ClassAssignmentRequest;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;
import IotSystem.IoTSystem.Service.IClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public ResponseEntity<ClassAssignmentResponse> create(@RequestBody ClassAssignmentRequest request) {

        ClassAssignmentResponse response = classAssignmentService.create(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ClassAssignment update(@PathVariable UUID id, @RequestBody ClassAssignment assignment) {
        return classAssignmentService.update(id, assignment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        classAssignmentService.delete(id);
    }
}