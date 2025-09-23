package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Service.KitsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
public class KitsController {
    @Autowired
    private KitsService kitsService;

    @Operation(summary = "Create KitComponent", description = "Create KitComponent")
    @PostMapping
    public ResponseEntity<Object> createKitComponent(@Valid @RequestBody KitRequest kitRequest) {
        kitsService.createKit(kitRequest);
        return ResponseEntity.ok( "Your Kit created successfully");
    }

    @Operation(summary = "Get KitComponent By KitComponentID", description = "Get KitComponent By DrugID")
    @GetMapping
    public ResponseEntity<Object> getKitComponentById(@RequestParam("id") Long id) {

        return ResponseEntity.ok(kitsService.getKitId(id));
    }

    @Operation(summary = "Get All Kits ", description = "Get All Kits")
    @GetMapping("/all")
    public ResponseEntity<List<KitResponse>> getAllKits() {
        List<KitResponse> kits = kitsService.getAllKits();
        return ResponseEntity.ok(kits);
    }

    @Operation(summary = "Delete Kit", description = "Delete Kit")
    @DeleteMapping
    public ResponseEntity<Object> deleteKitComponent(@RequestParam("id") Long id,
                                                     @Valid @RequestBody KitRequest kitRequest) {

        kitsService.deleteKit(id, kitRequest);
        return ResponseEntity.ok("Your Kit is Delete successfully");
    }


    @Operation(summary = "Update Kit By ID", description = "Get Kit By ID")
    @PutMapping
    public ResponseEntity<Object> updateKit(@RequestParam("id") Long id,
                                                     @Valid @RequestBody KitRequest kitRequest) {

        kitsService.updateKit(id, kitRequest);
        return ResponseEntity.ok("Your Kit is update successfully");
    }


}
