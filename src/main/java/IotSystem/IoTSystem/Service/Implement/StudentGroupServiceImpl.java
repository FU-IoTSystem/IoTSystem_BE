package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Service.IStudentGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentGroupServiceImpl implements IStudentGroupService {



    @Override
    public List<StudentGroup> getAll() {
        return List.of();
    }

    @Override
    public StudentGroup getById(UUID id) {
        return null;
    }

    @Override
    public StudentGroup create(StudentGroup group) {
        return null;
    }

    @Override
    public StudentGroup update(UUID id, StudentGroup group) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
//    @Autowired
//    private StudentGroupRepository studentGroupRepository;
//
//    public List<StudentGroup> getAll() {
//
//    }
//
//    public StudentGroup getById(UUID id) {
//        return studentGroupRepository.findById(id).orElse(null);
//    }
//
//    public StudentGroup create(StudentGroup group) {
//        return studentGroupRepository.save(group);
//    }
//
//    public StudentGroup update(UUID id, StudentGroup updated) {
//        StudentGroup existing = studentGroupRepository.findById(id).orElse(null);
//        if (existing != null) {
//            existing.setName(updated.getName());
//            existing.setClazz(updated.getClazz());
//            existing.setCreatedBy(updated.getCreatedBy());
//            existing.setStatus(updated.isStatus());
//            return studentGroupRepository.save(existing);
//        }
//        return null;
//    }
//
//    public void delete(UUID id) {
//        studentGroupRepository.deleteById(id);
//    }
}
