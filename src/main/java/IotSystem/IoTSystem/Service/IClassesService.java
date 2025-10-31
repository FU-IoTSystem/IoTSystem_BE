package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;

import java.util.List;
import java.util.UUID;

public interface IClassesService {
    List<Classes> getAll();

    Classes getById(UUID id);

    Classes create(Classes classes);

    Classes update(UUID id, Classes classes);

    void delete(UUID id);
}
