package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    private IClassesService service;

    @GetMapping("/get_All")
    public List<Classes> getAll() {
        return service.getAll();
    }

    @GetMapping("/getbyId/{id}")
    public Classes getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/post")
    public Classes create(@RequestBody Classes classes) {
        return service.create(classes);
    }

    @PutMapping("/update/{id}")
    public Classes update(@PathVariable UUID id, @RequestBody Classes classes) {
        return service.update(id, classes);
    }

    @DeleteMapping("//delete{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
