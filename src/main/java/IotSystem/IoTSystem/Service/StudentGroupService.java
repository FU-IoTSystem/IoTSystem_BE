package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.StudentGroup;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentGroupService {
    @Autowired
    private StudentGroupRepository repository;

    public List<StudentGroup> getAll() {
        return repository.findAll();
    }

    public StudentGroup getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public StudentGroup create(StudentGroup group) {
        return repository.save(group);
    }

    public StudentGroup update(UUID id, StudentGroup updated) {
        StudentGroup existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setClazz(updated.getClazz());
            existing.setCreatedBy(updated.getCreatedBy());
            existing.setStatus(updated.isStatus());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
