package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.ExcelImportRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Service.IExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = "*")
public class ExcelImportController {

    @Autowired
    private IExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<ExcelImportResponse> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("role") String role,
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

            // Validate role
            if (!role.equals("STUDENT") && !role.equals("LECTURER")) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponse.builder()
                                .success(false)
                                .message("Invalid role. Must be STUDENT or LECTURER")
                                .build()
                );
            }

            // Convert file to base64
            String fileContent = Base64.getEncoder().encodeToString(file.getBytes());

            // Create request
            ExcelImportRequest request = ExcelImportRequest.builder()
                    .role(role)
                    .fileName(file.getOriginalFilename())
                    .fileContent(fileContent)
                    .sheetName(sheetName)
                    .build();

            // Process import
            ExcelImportResponse response = excelImportService.importAccounts(request);

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

    @PostMapping("/import-base64")
    public ResponseEntity<ExcelImportResponse> importExcelBase64(@RequestBody ExcelImportRequest request) {
        try {
            // Validate request
            if (request.getFileContent() == null || request.getFileContent().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponse.builder()
                                .success(false)
                                .message("File content is required")
                                .build()
                );
            }

            if (request.getRole() == null || (!request.getRole().equals("STUDENT") && !request.getRole().equals("LECTURER"))) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponse.builder()
                                .success(false)
                                .message("Invalid role. Must be STUDENT or LECTURER")
                                .build()
                );
            }

            // Process import
            ExcelImportResponse response = excelImportService.importAccounts(request);

            return ResponseEntity.ok(response);

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