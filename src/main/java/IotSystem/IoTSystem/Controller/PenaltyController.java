package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.Penalty;
import IotSystem.IoTSystem.Service.PenaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @GetMapping("getAll")
    public List<Penalty> getAll() {
        return penaltyService.getAll();
    }

    @GetMapping("/{id}")
    public Penalty getById(@PathVariable UUID id) {
        return penaltyService.getById(id);
    }

    @PostMapping("/create")
    public Penalty create(@RequestBody Penalty penalty) {
        return penaltyService.create(penalty);
    }

    @PutMapping("/update/{id}")
    public Penalty update(@PathVariable UUID id, @RequestBody Penalty penalty) {
        return penaltyService.update(id, penalty);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        penaltyService.delete(id);
    }
}
