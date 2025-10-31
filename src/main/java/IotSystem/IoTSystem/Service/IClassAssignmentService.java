package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;

import java.util.List;
import java.util.UUID;

public interface IClassAssignmentService {
    List<ClassAssignment> getAll();

    ClassAssignment getById(UUID id);

    ClassAssignment create(ClassAssignment assignment);

    ClassAssignment update(UUID id, ClassAssignment assignment);

    void delete(UUID id);
}
