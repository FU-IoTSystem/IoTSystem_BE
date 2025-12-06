package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Response.ClassAssignmentResponse;

import java.util.List;

public class ClassAssignmentMapper {
    public static ClassAssignmentResponse toResponse(ClassAssignment assignment) {
        return ClassAssignmentResponse.builder()
                .id(assignment.getId())
                .classId(assignment.getClazz() != null ? assignment.getClazz().getId() : null)
                .classCode(assignment.getClazz() != null ? assignment.getClazz().getClassCode() : null)
                .accountId(assignment.getAccount() != null ? assignment.getAccount().getId() : null)
                .accountName(assignment.getAccount() != null ? assignment.getAccount().getFullName() : null)
                .accountEmail(assignment.getAccount() != null ? assignment.getAccount().getEmail() : null)
                .studentCode(assignment.getAccount() != null ? assignment.getAccount().getStudentCode() : null)
                .lecturerCode(assignment.getAccount() != null ? assignment.getAccount().getLecturerCode() : null)
                .roleName(assignment.getRole() != null ? assignment.getRole().getName() : null)
                .createdAt(assignment.getCreatedAt())
                .build();
    }

    public static List<ClassAssignmentResponse> toResponseList(List<ClassAssignment> assignments) {
        return assignments.stream()
                .map(ClassAssignmentMapper::toResponse)
                .toList();
    }
}
