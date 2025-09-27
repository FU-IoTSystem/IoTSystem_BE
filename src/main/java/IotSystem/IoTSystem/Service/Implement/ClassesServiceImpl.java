package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Service.IClassesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassesServiceImpl  implements IClassesService {

    @Override
    public List<Classes> getAll() {
        return List.of();
    }

    @Override
    public Classes getById(UUID id) {
        return null;
    }

    @Override
    public Classes create(Classes classes) {
        return null;
    }

    @Override
    public Classes update(UUID id, Classes classes) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
