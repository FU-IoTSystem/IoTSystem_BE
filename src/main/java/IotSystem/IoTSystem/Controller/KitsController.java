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

@RestController
@RequestMapping("/api/kits")
public class KitsController {

    @Autowired
    private KitsService kitsService;

    // Lấy tất cả kits
    @GetMapping
    public ResponseEntity<List<KitResponseDTO>> getAllKits() {
        List<KitResponseDTO> kits = kitsService.getAllKits();
        return ResponseEntity.ok(kits);
    }

    // Lấy kit theo id
    @GetMapping("/get/{id}")
    public ResponseEntity<KitResponseDTO> getKitById(@PathVariable Integer id) {
        KitResponseDTO kit = kitsService.getKitById(id);
        return ResponseEntity.ok(kit);
    }

    // Cập nhật kit
    @PutMapping("/update/{id}")
    public ResponseEntity<KitResponseDTO> updateKit(@PathVariable Integer id, @RequestBody Kits updatedKit) {
        KitResponseDTO updated = kitsService.updateKit(id, updatedKit);
        return ResponseEntity.ok(updated);
    }

    // Xoá kit
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteKit(@PathVariable Integer id) {
        kitsService.deleteKit(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<KitResponseDTO> updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(kitsService.updateKitStatus(id, request.getStatus()));
    }

    @PutMapping("/{id}/type")
    public ResponseEntity<KitResponseDTO> updateType(
            @PathVariable Integer id,
            @RequestBody UpdateTypeRequest request) {
        return ResponseEntity.ok(kitsService.updateKitType(id, request.getType()));
    }

}
