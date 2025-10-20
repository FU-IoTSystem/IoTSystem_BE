package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassesServiceImpl  implements IClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    @Override
    public List<Classes> getAll() {
        return classesRepository.findAll();
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
