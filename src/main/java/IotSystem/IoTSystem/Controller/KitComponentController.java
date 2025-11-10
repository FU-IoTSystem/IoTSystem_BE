package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Service.IKitComponentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kitComponent")
@RequiredArgsConstructor
public class KitComponentController {

    private final IKitComponentService kitComponentService;

    @Operation(summary = "Create KitComponent", description = "Create KitComponent")
    @PostMapping
    public ResponseEntity<KitComponentResponse> createKitComponent(@Valid @RequestBody KitComponentRequest kitComponentRequest) {
        KitComponentResponse kitComponentResponse = kitComponentService.createKitComponent(kitComponentRequest);
        return ResponseEntity.ok(kitComponentResponse);
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
    public ResponseEntity<KitResponse> deleteKitComponent(@RequestParam("id") UUID id) {
        KitResponse kitResponse = kitComponentService.deleteKitComponent(id);
        return ResponseEntity.ok(kitResponse);
    }


    @Operation(summary = "Update KitComponent By ID", description = "Get KitComponent By ID")
    @PutMapping
    public ResponseEntity<KitComponentResponse> updateKitComponent(@RequestParam("id") UUID id,
                                                                   @Valid @RequestBody KitComponentRequest kitComponentRequest) {
        KitComponentResponse kitComponentResponse = kitComponentService.updateKitComponent(id, kitComponentRequest);
        return ResponseEntity.ok(kitComponentResponse);
    }

}
