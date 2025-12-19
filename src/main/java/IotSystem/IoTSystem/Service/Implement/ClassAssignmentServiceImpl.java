package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Mappers.ClassAssignmentMapper;
import IotSystem.IoTSystem.Model.Mappers.ClassResponseMapper;
import IotSystem.IoTSystem.Model.Request.ClassAssignmentRequest;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;
import IotSystem.IoTSystem.Model.Response.ClassResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Service.IClassAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassAssignmentServiceImpl implements IClassAssignmentService {

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private RolesRepository rolesRepository;

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
        // Check if class exists
        Classes classes = classesRepository.findById(request.getClassId()).orElseThrow(()
                -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(()
                -> new ResourceNotFoundException("Did not found the Student/ Lecturer: " + request.getAccountId()));

        // Check if this is a lecturer assignment
        Roles lecturerRole = rolesRepository.findByName("LECTURER")
                .orElseThrow(() -> new ResourceNotFoundException("LECTURER role not found"));

        if (account.getRole() != null && account.getRole().getId().equals(lecturerRole.getId())) {
            // Check if class already has a lecturer assigned
            List<ClassAssignment> existingLecturerAssignments = classAssignemntRepository.findByClazz(classes).stream()
                    .filter(ca -> ca.getAccount() != null
                            && ca.getAccount().getRole() != null
                            && ca.getAccount().getRole().getId().equals(lecturerRole.getId()))
                    .collect(Collectors.toList());

            if (!existingLecturerAssignments.isEmpty()) {
                throw new RuntimeException("Class already has a lecturer assigned");
            }
        }

        // Check if assignment already exists
        Optional<ClassAssignment> existingAssignment = classAssignemntRepository.findByClazzAndAccount(classes, account);
        if (existingAssignment.isPresent()) {
            throw new RuntimeException("Assignment already exists for this class and account");
        }

        // For students: validate that old class is inactive before joining new class
        Roles studentRole = rolesRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("STUDENT role not found"));

        if (account.getRole() != null && account.getRole().getId().equals(studentRole.getId())) {
            // Get all existing class assignments for this student
            List<ClassAssignment> existingAssignments = classAssignemntRepository.findByAccount(account);

            // Check if student has any active class assignments
            boolean hasActiveClass = existingAssignments.stream()
                    .anyMatch(assignment -> assignment.getClazz() != null && assignment.getClazz().isStatus());

            if (hasActiveClass) {
                throw new RuntimeException("Student is already in an active class. Please wait until your current class becomes inactive before joining a new class.");
            }
        }

        ClassAssignment assignment = new ClassAssignment();
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
    @Transactional
    public void delete(UUID id) {
        ClassAssignment assignment = classAssignemntRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class assignment not found with id: " + id));

        // Get LECTURER role
        Roles lecturerRole = rolesRepository.findByName("LECTURER")
                .orElseThrow(() -> new ResourceNotFoundException("LECTURER role not found"));

        // Check if this is a lecturer assignment
        boolean isLecturerAssignment = assignment.getAccount() != null
                && assignment.getAccount().getRole() != null
                && assignment.getAccount().getRole().getId().equals(lecturerRole.getId());

        if (isLecturerAssignment) {
            // If deleting lecturer assignment, delete all lecturer assignments in that class
            // But keep student assignments (don't delete students)
            Classes clazz = assignment.getClazz();

            // Find all lecturer assignments related to this class
            List<ClassAssignment> lecturerAssignments = classAssignemntRepository.findByClazz(clazz).stream()
                    .filter(ca -> ca.getAccount() != null
                            && ca.getAccount().getRole() != null
                            && ca.getAccount().getRole().getId().equals(lecturerRole.getId()))
                    .collect(Collectors.toList());

            // Delete all lecturer assignments only (not student assignments)
            classAssignemntRepository.deleteAll(lecturerAssignments);
        } else {
            // If deleting student assignment, just delete the assignment itself
            // Don't delete the student account
            classAssignemntRepository.delete(assignment);
        }
    }

    @Override
    public List<ClassResponse> getUnassignedClasses() {
        // Get LECTURER role
        Roles lecturerRole = rolesRepository.findByName("LECTURER")
                .orElseThrow(() -> new ResourceNotFoundException("LECTURER role not found"));

        // Get all classes
        List<Classes> allClasses = classesRepository.findAll();

        // Get all class assignments with lecturer role
        List<ClassAssignment> lecturerAssignments = classAssignemntRepository.findAll().stream()
                .filter(ca -> ca.getAccount() != null
                        && ca.getAccount().getRole() != null
                        && ca.getAccount().getRole().getId().equals(lecturerRole.getId()))
                .collect(Collectors.toList());

        // Get IDs of classes that have lecturer assignments
        Set<UUID> assignedClassIds = lecturerAssignments.stream()
                .map(ca -> ca.getClazz().getId())
                .collect(Collectors.toSet());

        // Filter classes that don't have lecturer assignments
        List<Classes> unassignedClasses = allClasses.stream()
                .filter(clazz -> !assignedClassIds.contains(clazz.getId()))
                .collect(Collectors.toList());

        // Convert to ClassResponse
        return unassignedClasses.stream()
                .map(clazz -> {
                    ClassResponse response = new ClassResponse();
                    response.setId(clazz.getId());
                    response.setClassCode(clazz.getClassCode());
                    response.setSemester(clazz.getSemester());
                    response.setStatus(clazz.isStatus());
                    response.setCreatedAt(clazz.getCreatedAt());
                    response.setUpdatedAt(clazz.getUpdatedAt());

                    // Set teacher info if exists (from Classes.account which is the teacher who created the class)
                    if (clazz.getAccount() != null) {
                        response.setTeacherId(clazz.getAccount().getId());
                        response.setTeacherName(clazz.getAccount().getFullName());
                        response.setTeacherEmail(clazz.getAccount().getEmail());
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }
}
