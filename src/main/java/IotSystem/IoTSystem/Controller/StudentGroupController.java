package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;

import IotSystem.IoTSystem.Model.Request.StudentGroupRequest;
import IotSystem.IoTSystem.Model.Response.StudentGroupResponse;
import IotSystem.IoTSystem.Service.IStudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupController {
    @Autowired
    private IStudentGroupService studentGroupService;

    @GetMapping("/getAll")
    public ResponseEntity<List<StudentGroupResponse>> getAll() {
        return ResponseEntity.ok(studentGroupService.getAll());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<StudentGroupResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentGroupService.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<StudentGroupResponse> create(@RequestBody StudentGroupRequest request) {
        StudentGroupResponse group = studentGroupService.create(request);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/update/{id}")
    public StudentGroup update(@PathVariable UUID id, @RequestBody StudentGroup group) {
        return studentGroupService.update(id, group);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        studentGroupService.delete(id);
    }
}
