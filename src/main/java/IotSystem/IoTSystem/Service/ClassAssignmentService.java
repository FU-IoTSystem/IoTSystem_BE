package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.Entities.ClassAssignment;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassAssignmentService {

    @Autowired
    private ClassAssignemntRepository repository;

    public List<ClassAssignment> getAll() {
        return repository.findAll();
    }

    public ClassAssignment getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public ClassAssignment create(ClassAssignment assignment) {
        return repository.save(assignment);
    }

    public ClassAssignment update(UUID id, ClassAssignment updated) {
        ClassAssignment existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setClazz(updated.getClazz());
            existing.setAccount(updated.getAccount());
            existing.setRole(updated.getRole());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
