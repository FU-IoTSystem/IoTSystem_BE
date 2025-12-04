package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Request.ClassRequest;
import IotSystem.IoTSystem.Model.Response.ClassResponse;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import IotSystem.IoTSystem.Service.IAccountService;
import IotSystem.IoTSystem.Service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    private IClassesService service;

    @Autowired
    private IAccountService accountService;

    @GetMapping("/get_All")
    public ResponseEntity<List<ClassResponse>> getAll() {
        List<ClassResponse> responses = service.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/getbyId/{id}")
    public Classes getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/post/{teacherID}")
    public ResponseEntity<ClassResponse> create(@RequestBody ClassRequest request, @PathVariable UUID teacherID) {
        ClassResponse response = service.create(request, teacherID);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getListLecturers")
    public ResponseEntity<List<ProfileResponse>> getAllLecturers(){
        List<ProfileResponse> response = accountService.getAllbyRoleLecture();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ClassResponse> update(@PathVariable UUID id, @RequestBody ClassRequest request) {
        ClassResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete class: " + e.getMessage());
        }
    }
}
