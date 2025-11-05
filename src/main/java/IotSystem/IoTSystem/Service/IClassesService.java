package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Request.ClassRequest;
import IotSystem.IoTSystem.Model.Response.ClassResponse;

import java.util.List;
import java.util.UUID;

public interface IClassesService {
    List<ClassResponse> getAll();

    Classes getById(UUID id);

    ClassResponse create(ClassRequest request, UUID teacherID);

    ClassResponse update(UUID id, ClassRequest request);

    void delete(UUID id);
}