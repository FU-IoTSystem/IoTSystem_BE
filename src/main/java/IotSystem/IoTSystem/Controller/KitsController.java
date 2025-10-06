package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.KitCreationRequest;
import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Service.IKitsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kits")
public class KitsController {
    @Autowired
    private IKitsService kitsService;


    @GetMapping("/{kitId}")
    @Operation(summary = "Lấy thông tin chi tiết của một Kit (bao gồm các component)")
    public ResponseEntity<ApiResponse<KitResponse>> getKitById(@PathVariable("kitId") UUID kitId) {
        KitResponse kitResponse = kitsService.getKitById(kitId);

        ApiResponse<KitResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Lấy thông tin Kit thành công");
        response.setData(kitResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo mới một Kit kèm các component")
    public ResponseEntity<ApiResponse<KitResponse>> createKit(@RequestBody @Valid KitCreationRequest request) {
        KitResponse kitResponse = kitsService.createKitWithComponents(request);

        ApiResponse<KitResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Tạo Kit thành công");
        response.setData(kitResponse);

        return ResponseEntity.ok(response);
    }
}

