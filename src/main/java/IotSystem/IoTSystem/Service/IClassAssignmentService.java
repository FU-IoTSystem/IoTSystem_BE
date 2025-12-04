package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Request.ClassAssignmentRequest;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;
import IotSystem.IoTSystem.Model.Response.ClassResponse;

import java.util.List;
import java.util.UUID;

public interface IClassAssignmentService {
    List<ClassAssignmentResponse> getAll();

    ClassAssignmentResponse getById(UUID id);

    ClassAssignmentResponse create(ClassAssignmentRequest request);

    ClassAssignment update(UUID id, ClassAssignment assignment);

    void delete(UUID id);

    // Get list of classes that haven't been assigned to any lecturer
    List<ClassResponse> getUnassignedClasses();
}
