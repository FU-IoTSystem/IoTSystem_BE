package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import IotSystem.IoTSystem.Model.Mappers.ClassResponseMapper;
import IotSystem.IoTSystem.Model.Request.ClassRequest;
import IotSystem.IoTSystem.Model.Response.ClassResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Repository.BorrowingGroupRepository;
import IotSystem.IoTSystem.Service.IAccountService;
import IotSystem.IoTSystem.Service.IClassesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ClassesServiceImpl  implements IClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private BorrowingGroupRepository borrowingGroupRepository;

    @Override
    public List<ClassResponse> getAll() {
        List<Classes> classes = classesRepository.findAll();
        return classes.stream().map(clas ->
                ClassResponseMapper.toResponse(clas, clas.getAccount() != null ? clas.getAccount().getId() : null)).toList();
    }

    @Override
    public Classes getById(UUID id) {
        return null;
    }

    @Override
    public ClassResponse create(ClassRequest request, UUID teacherID) {
        // Validate classCode uniqueness
        if (request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            String classCode = request.getClassCode().trim();
            if (classesRepository.existsByClassCode(classCode)) {
                throw new RuntimeException("Class Code already exists: " + classCode);
            }
        }

        Classes classes = new Classes();

        classes.setClassCode(request.getClassCode());
        classes.setSemester(request.getSemester());
        classes.setStatus(request.isStatus());

        Account account = accountRepository.findById(teacherID)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found the Lecturer: " + teacherID));
        classes.setAccount(account);

        classesRepository.save(classes);

        return ClassResponseMapper.toResponse(classes, classes.getAccount().getId());
    }

    @Override
    @Transactional
    public ClassResponse update(UUID id, ClassRequest request) {
        Classes editing = classesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Did not found the Class: " + id));
        Account account = accountRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Did not found the Lecturer: " + request.getTeacherId()));

        // Validate classCode uniqueness (excluding current class)
        if (request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            String newClassCode = request.getClassCode().trim();
            // Check if classCode is being changed
            if (editing.getClassCode() == null || !newClassCode.equals(editing.getClassCode())) {
                if (classesRepository.existsByClassCodeExcludingId(newClassCode, id)) {
                    throw new RuntimeException("Class Code already exists: " + newClassCode);
                }
            }
        }

        // Check if class status is being changed to inactive
        boolean wasActive = editing.isStatus();
        boolean willBeInactive = !request.isStatus();
        boolean isCurrentlyInactive = !editing.isStatus();

        editing.setAccount(account);
        editing.setSemester(request.getSemester());
        editing.setStatus(request.isStatus());
        editing.setClassCode(request.getClassCode());

        classesRepository.save(editing);

        // If class is being set to inactive (from active to inactive), disable all groups
        if (wasActive && willBeInactive) {
            disableGroupsForInactiveClass(editing);
        }
        // If class is being set to active (from inactive to active), enable all groups
        else if (!wasActive && !willBeInactive) {
            enableGroupsForActiveClass(editing);
        }
        // If class is already inactive and being edited, ensure all related groups are also inactive
        else if (isCurrentlyInactive && willBeInactive) {
            ensureGroupsAreInactiveForInactiveClass(editing);
        }

        return ClassResponseMapper.toResponse(editing, editing.getAccount().getId());
    }

    /**
     * Disable all groups in an inactive class and convert leaders to members
     * When a class becomes inactive:
     * - All student groups in that class are disabled
     * - All leaders in those groups are converted to members
     * - All BorrowingGroups have isActive set to false
     */
    private void disableGroupsForInactiveClass(Classes clazz) {
        // Find all student groups in this class
        List<StudentGroup> groups = studentGroupRepository.findByClazz(clazz);

        for (StudentGroup group : groups) {
            // Disable the group
            group.setStatus(false);
            studentGroupRepository.save(group);
            log.info("Disabled group {} for inactive class {}", group.getGroupName(), clazz.getClassCode());

            // Find all borrowing groups (members) in this student group
            List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupId(group.getId());

            // Convert all leaders to members and set isActive to false for all members
            for (BorrowingGroup borrowingGroup : borrowingGroups) {
                if (borrowingGroup.getRoles() == GroupRoles.LEADER) {
                    borrowingGroup.setRoles(GroupRoles.MEMBER);
                    log.info("Converted leader {} to member in group {}",
                            borrowingGroup.getAccount() != null ? borrowingGroup.getAccount().getEmail() : "unknown",
                            group.getGroupName());
                }
                // Set isActive to false for all BorrowingGroups when class becomes inactive
                borrowingGroup.setActive(false);
                borrowingGroupRepository.save(borrowingGroup);
                log.info("Set isActive=false for borrowing group {} in group {}",
                        borrowingGroup.getAccount() != null ? borrowingGroup.getAccount().getEmail() : "unknown",
                        group.getGroupName());
            }
        }
    }

    /**
     * Enable all groups in an active class
     * When a class becomes active:
     * - All student groups in that class are enabled
     * - All BorrowingGroups have isActive set to true
     * Note: Leaders are NOT automatically restored as the role information was lost
     */
    private void enableGroupsForActiveClass(Classes clazz) {
        // Find all student groups in this class
        List<StudentGroup> groups = studentGroupRepository.findByClazz(clazz);

        for (StudentGroup group : groups) {
            // Enable the group
            group.setStatus(true);
            studentGroupRepository.save(group);
            log.info("Enabled group {} for active class {}", group.getGroupName(), clazz.getClassCode());

            // Find all borrowing groups (members) in this student group
            List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupId(group.getId());

            // Set isActive to true for all BorrowingGroups
            for (BorrowingGroup borrowingGroup : borrowingGroups) {
                borrowingGroup.setActive(true);
                borrowingGroupRepository.save(borrowingGroup);
                log.info("Set isActive=true for borrowing group {} in group {}",
                        borrowingGroup.getAccount() != null ? borrowingGroup.getAccount().getEmail() : "unknown",
                        group.getGroupName());
            }
        }
    }

    /**
     * Ensure all groups related to an inactive class are also inactive
     * This is called when editing an already inactive class to ensure consistency
     * - All student groups in that class are set to inactive
     * - All BorrowingGroups have isActive set to false
     */
    private void ensureGroupsAreInactiveForInactiveClass(Classes clazz) {
        // Find all student groups in this class
        List<StudentGroup> groups = studentGroupRepository.findByClazz(clazz);

        for (StudentGroup group : groups) {
            // Ensure the group is inactive
            if (group.isStatus()) {
                group.setStatus(false);
                studentGroupRepository.save(group);
                log.info("Set group {} to inactive for inactive class {}", group.getGroupName(), clazz.getClassCode());
            }

            // Find all borrowing groups (members) in this student group
            List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupId(group.getId());

            // Ensure all BorrowingGroups have isActive = false
            for (BorrowingGroup borrowingGroup : borrowingGroups) {
                if (borrowingGroup.isActive()) {
                    borrowingGroup.setActive(false);
                    borrowingGroupRepository.save(borrowingGroup);
                    log.info("Set isActive=false for borrowing group {} in group {} (class already inactive)",
                            borrowingGroup.getAccount() != null ? borrowingGroup.getAccount().getEmail() : "unknown",
                            group.getGroupName());
                }
            }
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        // Find the class
        Classes clazz = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        // Delete all class assignments related to this class
        List<ClassAssignment> assignments = classAssignemntRepository.findByClazz(clazz);
        if (!assignments.isEmpty()) {
            classAssignemntRepository.deleteAll(assignments);
            log.info("Deleted {} class assignments for class {}", assignments.size(), clazz.getClassCode());
        }

        // Delete the class
        classesRepository.delete(clazz);
        log.info("Deleted class with id: {}", id);
    }
}
