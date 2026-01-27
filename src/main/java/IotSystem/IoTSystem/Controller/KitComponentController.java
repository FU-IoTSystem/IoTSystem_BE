package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Service.IKitComponentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
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

    @Operation(summary = "Get Components Without Kit", description = "Get all components that are not assigned to any kit")
    @GetMapping("/no-kit")
    public ResponseEntity<List<KitComponentResponse>> getComponentsWithoutKit() {
        List<KitComponentResponse> components = kitComponentService.getKitComponentsWithoutKit();
        return ResponseEntity.ok(components);
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

    @Operation(summary = "Import Components from Excel", description = "Import kit components from Excel file with columns: index, name, link, quantity")
    @PostMapping("/import")
    public ResponseEntity<ExcelImportResponse> importComponents(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "kitId", required = false) UUID kitId,
            @RequestParam(value = "sheetName", required = false) String sheetName) {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponse.builder()
                                .success(false)
                                .message("File is empty")
                                .build()
                );
            }

            // Validate file extension
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponse.builder()
                                .success(false)
                                .message("Invalid file format. Please upload a .xls or .xlsx file.")
                                .build()
                );
            }

            // Convert file to base64
            String fileContent = Base64.getEncoder().encodeToString(file.getBytes());

            // Process import
            ExcelImportResponse response = kitComponentService.importComponentsFromExcel(kitId, fileContent, fileName, sheetName);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                    ExcelImportResponse.builder()
                            .success(false)
                            .message("Error reading file: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ExcelImportResponse.builder()
                            .success(false)
                            .message("Unexpected error: " + e.getMessage())
                            .build()
            );
        }
    }

}
