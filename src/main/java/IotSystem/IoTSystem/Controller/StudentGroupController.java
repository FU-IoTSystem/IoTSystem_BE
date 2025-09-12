package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.StudentGroup;
import IotSystem.IoTSystem.Service.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupController {
    @Autowired
    private StudentGroupService service;

    @GetMapping("/getAll")
    public List<StudentGroup> getAll() {
        return service.getAll();
    }

    @GetMapping("/getById/{id}")
    public StudentGroup getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public StudentGroup create(@RequestBody StudentGroup group) {
        return service.create(group);
    }

    @PutMapping("/update/{id}")
    public StudentGroup update(@PathVariable UUID id, @RequestBody StudentGroup group) {
        return service.update(id, group);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
