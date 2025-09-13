package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.ClassAssignment;
import IotSystem.IoTSystem.Service.ClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/class-assignments")
public class ClassAssignmentController {

    @Autowired
    private ClassAssignmentService service;

    @GetMapping
    public List<ClassAssignment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ClassAssignment getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ClassAssignment create(@RequestBody ClassAssignment assignment) {
        return service.create(assignment);
    }

    @PutMapping("/{id}")
    public ClassAssignment update(@PathVariable UUID id, @RequestBody ClassAssignment assignment) {
        return service.update(id, assignment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
