package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.Enum.Status.ErrorCode;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.*;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
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

    @GetMapping("/")
    public ResponseEntity<List<KitResponse>> getAllKit(){
        List<KitResponse> responses = kitsService.getAllKits();
        return ResponseEntity.ok(responses);
    };

    @GetMapping("/student")
    public ResponseEntity<List<KitResponse>> getAllKitsForStudent(){
        List<KitResponse> responses = kitsService.getAllKitsForStudent();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{kitId}")
    public ResponseEntity<KitResponse> updateKit(@PathVariable UUID kitId, @RequestBody KitRequest request){
        KitResponse response = kitsService.updateKit(kitId, request);
        return ResponseEntity.ok(response);
    }

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

    @PostMapping("/create-single")
    @Operation(summary = "Tạo mới một Kit đơn lẻ không kèm component")
    public ResponseEntity<ApiResponse<KitResponse>> createSingleKit(@RequestBody @Valid KitSingleCreateRequest request) {
        KitResponse kitResponse = kitsService.createSingleKit(request);
        ApiResponse<KitResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Tạo Kit đơn lẻ thành công");
        response.setData(kitResponse);
        return ResponseEntity.ok(response);
    }

    // ✅ Thêm một component vào Kit
    @PostMapping("/add-one")
    public ResponseEntity<ApiResponse<KitComponentResponse>> addSingleComponent(@RequestBody AddSingleComponentRequest request) {
        KitComponentResponse result = kitsService.addSingleComponentToKit(request);

        ApiResponse<KitComponentResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Created);
        response.setErrorCode(ErrorCode.Success);
        response.setSuccess(true);
        response.setStatusText("Component added successfully");
        response.setMessage("Thêm component thành công");
        response.setTotal(1);
        response.setData(result);

        return ResponseEntity.status(response.getStatus().getCode()).body(response);
    }

    // ✅ Thêm nhiều component vào Kit
    @PostMapping("/add-many")
    public ResponseEntity<ApiResponse<List<KitComponentResponse>>> addMultipleComponents(@RequestBody AddMultipleComponentsRequest request) {
        List<KitComponentResponse> result = kitsService.addMultipleComponentsToKit(request);

        ApiResponse<List<KitComponentResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Created);
        response.setErrorCode(ErrorCode.Success);
        response.setSuccess(true);
        response.setStatusText("Components added successfully");
        response.setMessage("Thêm nhiều component thành công");
        response.setTotal(result.size());
        response.setData(result);

        return ResponseEntity.status(response.getStatus().getCode()).body(response);
    }

    @DeleteMapping("/{kitId}")
    @Operation(summary = "Xóa một Kit")
    public ResponseEntity<ApiResponse<Void>> deleteKit(@PathVariable("kitId") UUID kitId) {
        try {
            kitsService.deleteKit(kitId);

            ApiResponse<Void> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Xóa Kit thành công");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to delete kit: " + e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
