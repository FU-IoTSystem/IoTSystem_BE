package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Service.ClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/class-assignments")
public class ClassAssignmentController {

    @Autowired
    @Qualifier("classAssignmentServiceImpl")
    private ClassAssignmentService classAssignmentService;

    @GetMapping
    public List<ClassAssignment> getAll() {
        return classAssignmentService.getAll();
    }

    @GetMapping("/{id}")
    public ClassAssignment getById(@PathVariable UUID id) {
        return classAssignmentService.getById(id);
    }

    @PostMapping
    public ClassAssignment create(@RequestBody ClassAssignment assignment) {
        return classAssignmentService.create(assignment);
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
