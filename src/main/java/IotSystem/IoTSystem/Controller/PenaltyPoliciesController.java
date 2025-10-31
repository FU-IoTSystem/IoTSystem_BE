package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Service.IPenaltyPoliciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalty-policies")
public class PenaltyPoliciesController {

    @Autowired
    private IPenaltyPoliciesService service;

    @GetMapping("/getAll")
    public List<PenaltyPolicies> getAll() {
        return service.getAll();
    }

    @GetMapping("/getById/{id}")
    public PenaltyPolicies getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public PenaltyPolicies create(@RequestBody PenaltyPolicies policy) {
        return service.create(policy);
    }

    @PutMapping("/update/{id}")
    public PenaltyPolicies update(@PathVariable UUID id, @RequestBody PenaltyPolicies policy) {
        return service.update(id, policy);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
