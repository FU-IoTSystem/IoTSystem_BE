package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.PenaltyPoliciesResponse;
import IotSystem.IoTSystem.Service.IPenaltyPoliciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalty-policies")
public class PenaltyPoliciesController {

    @Autowired
    private IPenaltyPoliciesService service;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<PenaltyPoliciesResponse>>> getAll() {

        List<PenaltyPoliciesResponse> policies = service.getAll();

        ApiResponse<List<PenaltyPoliciesResponse>> response = new ApiResponse<>();

        response.setMessage("Fetch Policies successfully");
        response.setStatus(HTTPStatus.Ok);
        response.setData(policies);


        return ResponseEntity.ok(response);
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
