package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Service.IClassAssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassAssignmentServiceImpl implements IClassAssignmentService {
    @Override
    public List<ClassAssignment> getAll() {
        return List.of();
    }

    @Override
    public ClassAssignment getById(UUID id) {
        return null;
    }

    @Override
    public ClassAssignment create(ClassAssignment assignment) {
        return null;
    }

    @Override
    public ClassAssignment update(UUID id, ClassAssignment assignment) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
