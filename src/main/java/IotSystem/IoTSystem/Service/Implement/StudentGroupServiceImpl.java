package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Mappers.StudentGroupMapper;
import IotSystem.IoTSystem.Model.Request.StudentGroupRequest;
import IotSystem.IoTSystem.Model.Response.StudentGroupResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Service.IStudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentGroupServiceImpl implements IStudentGroupService {


    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<StudentGroupResponse> getAll() {
        List<StudentGroup> responses = studentGroupRepository.findAll();
        return responses.stream().map(StudentGroupMapper::toResponse).toList();
    }

    @Override
    public StudentGroupResponse getById(UUID id) {
        StudentGroup studentGroup = studentGroupRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Class: " + id));
        return StudentGroupMapper.toResponse(studentGroup);
    }

    @Override
    public StudentGroupResponse create(StudentGroupRequest request) {
        Classes classes = classesRepository.findById(request.getClassId()).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Class: " + request.getClassId()));

        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(()
                -> new ResourceNotFoundException("Did not found this Account: " + request.getAccountId()));

        StudentGroup group = StudentGroupMapper.toEntity(request, classes, account);

        studentGroupRepository.save(group);

        return StudentGroupMapper.toResponse(group);
    }

    @Override
    public StudentGroup update(UUID id, StudentGroup group) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        StudentGroup studentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found StudentGroup with ID: " + id));

        // Delete the student group (cascade will automatically delete related borrowing groups)
        studentGroupRepository.deleteById(id);
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
