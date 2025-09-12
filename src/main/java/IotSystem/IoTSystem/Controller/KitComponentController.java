package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.Kit_Component;
import IotSystem.IoTSystem.Service.KitComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kit-components")
public class KitComponentController {


    @Autowired
    private KitComponentService service;

    @GetMapping("/getAll")
    public List<Kit_Component> getAll() {
        return service.getAll();
    }

    @GetMapping("/getById/{id}")
    public Kit_Component getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("post")
    public Kit_Component create(@RequestBody Kit_Component component) {
        return service.create(component);
    }

    @PutMapping("/update/{id}")
    public Kit_Component update(@PathVariable UUID id, @RequestBody Kit_Component component) {
        return service.update(id, component);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
