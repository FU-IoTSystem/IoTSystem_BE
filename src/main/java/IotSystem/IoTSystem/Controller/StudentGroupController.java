package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;

import IotSystem.IoTSystem.Service.IStudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupController {
    @Autowired
    private IStudentGroupService studentGroupService;

    @GetMapping("/getAll")
    public List<StudentGroup> getAll() {
        return studentGroupService.getAll();
    }

    @GetMapping("/getById/{id}")
    public StudentGroup getById(@PathVariable UUID id) {
        return studentGroupService.getById(id);
    }

    @PostMapping("/create")
    public StudentGroup create(@RequestBody StudentGroup group) {
        return studentGroupService.create(group);
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
