package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.DTOs.Response.KitResponseDTO;
import IotSystem.IoTSystem.DTOs.UpdateStatusRequest;
import IotSystem.IoTSystem.DTOs.UpdateTypeRequest;
import IotSystem.IoTSystem.Entities.Kits;
import IotSystem.IoTSystem.Service.KitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kits")
public class KitsController {

    @Autowired
    private KitsService kitsService;

    @GetMapping
    public List<Kits> getAll() {
        return kitsService.getAll();
    }

    @GetMapping("/{id}")
    public Kits getById(@PathVariable UUID id) {
        return kitsService.getById(id);
    }

    @PostMapping
    public Kits create(@RequestBody Kits kit) {
        return kitsService.create(kit);
    }

    @PutMapping("/{id}")
    public Kits update(@PathVariable UUID id, @RequestBody Kits kit) {
        return kitsService.update(id, kit);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        kitsService.delete(id);
    }
}
