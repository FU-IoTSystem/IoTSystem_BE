package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Service.Implement.StudentImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/classes")
public class ClassImportController {

    @Autowired
    private StudentImportService studentImportService;

    @PostMapping(value = "/import-students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelImportResponse> importStudents(@RequestParam("file") MultipartFile file) {
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

            // Process import
            int createdCount = studentImportService.importStudentsFromSpreadsheetML(file);

            return ResponseEntity.ok(
                    ExcelImportResponse.builder()
                            .success(true)
                            .successCount(createdCount)
                            .message("Imported new students: " + createdCount)
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


