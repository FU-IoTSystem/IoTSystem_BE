package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.ClassAssignment;
import IotSystem.IoTSystem.Service.ClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/class-assignments")
public class ClassesController {

    @Autowired
    private ClassAssignmentService service;

    @GetMapping("/get_All")
    public List<ClassAssignment> getAll() {
        return service.getAll();
    }

    @GetMapping("/getbyId/{id}")
    public ClassAssignment getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/post")
    public ClassAssignment create(@RequestBody ClassAssignment assignment) {
        return service.create(assignment);
    }

    @PutMapping("/update/{id}")
    public ClassAssignment update(@PathVariable UUID id, @RequestBody ClassAssignment assignment) {
        return service.update(id, assignment);
    }

    @DeleteMapping("//delete{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
