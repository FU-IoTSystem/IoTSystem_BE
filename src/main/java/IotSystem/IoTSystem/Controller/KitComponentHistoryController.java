package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.KitComponentHistoryRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentHistoryResponse;
import IotSystem.IoTSystem.Service.IKitComponentHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kit-component-history")
@RequiredArgsConstructor
public class KitComponentHistoryController {

    private final IKitComponentHistoryService kitComponentHistoryService;

    @PostMapping
    public ResponseEntity<KitComponentHistoryResponse> create(@Valid @RequestBody KitComponentHistoryRequest request) {
        KitComponentHistoryResponse response = kitComponentHistoryService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<KitComponentHistoryResponse>> getAll() {
        List<KitComponentHistoryResponse> responses = kitComponentHistoryService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/kit/{kitId}")
    public ResponseEntity<List<KitComponentHistoryResponse>> getByKit(@PathVariable UUID kitId) {
        List<KitComponentHistoryResponse> responses = kitComponentHistoryService.getByKitId(kitId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/component/{componentId}")
    public ResponseEntity<List<KitComponentHistoryResponse>> getByComponent(@PathVariable UUID componentId) {
        List<KitComponentHistoryResponse> responses = kitComponentHistoryService.getByComponentId(componentId);
        return ResponseEntity.ok(responses);
    }
}

