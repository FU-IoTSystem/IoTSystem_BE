package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Request.PenaltyRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.PenaltyResponse;
import IotSystem.IoTSystem.Service.IPenaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private IPenaltyService penaltyService;

    @GetMapping("/getPenByAccount")
    public ResponseEntity<ApiResponse<List<PenaltyResponse>>> getPenByAccount() {

        ApiResponse<List<PenaltyResponse>> response = new ApiResponse<>();

        List<PenaltyResponse> penaltyResponses = penaltyService.getPenaltyByAccount();

        response.setMessage("Fetch get penalties successfully");
        response.setStatus(HTTPStatus.Ok);
        response.setData(penaltyResponses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllByResolved-F")
    public ResponseEntity<ApiResponse<List<PenaltyResponse>>> getResolvedByFalse(){

        ApiResponse<List<PenaltyResponse>> response = new ApiResponse<>();

        List<PenaltyResponse> penaltyResponses = penaltyService.getAll(false);

        response.setData(penaltyResponses);
        response.setMessage("Fetch penalties resolved by false successfully");
        response.setStatus(HTTPStatus.Ok);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public Penalty getById(@PathVariable UUID id) {
        return penaltyService.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PenaltyResponse>> create(@RequestBody PenaltyRequest request) {

        ApiResponse<PenaltyResponse> response = new ApiResponse<>();
        PenaltyResponse penaltyResponse = penaltyService.create(request);

        response.setData(penaltyResponse);
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Create penalty successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public Penalty update(@PathVariable UUID id, @RequestBody Penalty penalty) {
        return penaltyService.update(id, penalty);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        penaltyService.delete(id);
    }

    @PostMapping("/confirm-payment/{penaltyId}")
    public ResponseEntity<ApiResponse<String>> confirmPaymentForPenalty(@PathVariable UUID penaltyId) {
        try {
            penaltyService.confirmPaymentForPenalty(penaltyId);
            ApiResponse<String> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Penalty payment confirmed, wallet deducted, penalty resolved!");
            response.setData("Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.BadRequest);
            response.setMessage(e.getMessage());
            response.setData("Error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
