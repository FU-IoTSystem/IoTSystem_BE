package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.DTOs.KitComponentRequest;
import IotSystem.IoTSystem.DTOs.KitRequest;
import IotSystem.IoTSystem.DTOs.Response.KitComponentResponse;
import IotSystem.IoTSystem.DTOs.Response.KitResponse;
import IotSystem.IoTSystem.Service.Implement.KitComponentServiceImpl;
import IotSystem.IoTSystem.Service.KitComponentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitComponent")
@RequiredArgsConstructor
public class KitComponentController {

    private final KitComponentService kitComponentService;

    @Operation(summary = "Create KitComponent", description = "Create KitComponent")
    @PostMapping
    public ResponseEntity<Object> createKitComponent(@Valid @RequestBody KitComponentRequest kitComponentRequest) {
        kitComponentService.createKitComponent(kitComponentRequest);
        return ResponseEntity.ok( "Your KitComponent created successfully");
    }

    @Operation(summary = "Get KitComponent By KitComponentID", description = "Get KitComponent By DrugID")
    @GetMapping
    public ResponseEntity<Object> getKitComponentById(@RequestParam("id") Long id) {

        return ResponseEntity.ok(kitComponentService.getKitComponentId(id));
    }

    @Operation(summary = "Get All KitComponent ", description = "Get All KitComponent")
    @GetMapping("/all")
    public ResponseEntity<List<KitComponentResponse>> getAllKitComponent() {
        List<KitComponentResponse> kitComponents = kitComponentService.getAllKitComponents();
        return ResponseEntity.ok(kitComponents);
    }

    @Operation(summary = "Delete KitComponent", description = "Delete KitComponent")
    @DeleteMapping
    public ResponseEntity<Object> deleteKitComponent(@RequestParam("id") Long id,
                                             @Valid @RequestBody KitComponentRequest kitComponentRequest) {

        kitComponentService.deleteKitComponent(id, kitComponentRequest);
        return ResponseEntity.ok("Your KitComponent is Delete successfully");
    }


    @Operation(summary = "Update KitComponent By ID", description = "Get KitComponent By ID")
    @PutMapping
    public ResponseEntity<Object> updateKitComponent(@RequestParam("id") Long id,
                                             @Valid @RequestBody KitComponentRequest kitComponentRequest) {

        kitComponentService.updateKitComponent(id, kitComponentRequest);
        return ResponseEntity.ok("Your KitComponent is update successfully");
    }

}
