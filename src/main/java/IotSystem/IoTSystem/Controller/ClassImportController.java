package IotSystem.IoTSystem.Controller;

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
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        int createdCount = studentImportService.importStudentsFromSpreadsheetML(file);
        return ResponseEntity.ok("Imported new students: " + createdCount);
    }
}