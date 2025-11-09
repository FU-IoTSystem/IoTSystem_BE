package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Mappers.ClassAssignmentMapper;
import IotSystem.IoTSystem.Model.Request.ClassAssignmentRequest;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Service.IClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ClassAssignmentServiceImpl implements IClassAssignmentService {

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClassesRepository classesRepository;
    @Override
    public List<ClassAssignmentResponse> getAll() {
        List<ClassAssignment> classAssignment = classAssignemntRepository.findAll();
        return ClassAssignmentMapper.toResponseList(classAssignment);
    }

    @Override
    public ClassAssignmentResponse getById(UUID id) {
        ClassAssignment assignment = classAssignemntRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Lecturer: " + id));
        return ClassAssignmentMapper.toResponse(assignment);
    }

    @Override
    public ClassAssignmentResponse create(ClassAssignmentRequest request) { // create assignment
        ClassAssignment assignment = new ClassAssignment();

        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Student/ Lecturer: " + request.getAccountId()));

        Classes classes = classesRepository.findById(request.getClassId()).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Class: " + request.getClassId()));

        assignment.setAccount(account);
        assignment.setRole(account.getRole());
        assignment.setClazz(classes);

        classAssignemntRepository.save(assignment);
        return ClassAssignmentMapper.toResponse(assignment);
    }

    @Override
    public ClassAssignment update(UUID id, ClassAssignment assignment) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}