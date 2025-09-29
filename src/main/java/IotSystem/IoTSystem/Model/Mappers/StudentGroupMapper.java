package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Request.StudentGroupRequest;
import IotSystem.IoTSystem.Model.Response.StudentGroupResponse;
import org.springframework.stereotype.Component;

@Component
public class StudentGroupMapper {

    public StudentGroup toEntity(StudentGroupRequest request, Classes clazz, Account account) {
        StudentGroup group = new StudentGroup();
        group.setGroupName(request.getGroupName());
        group.setClazz(clazz);
        group.setAccount(account);
        group.setStatus(request.isStatus());
        group.setRoles(request.getRoles());
        return group;
    }

    public StudentGroupResponse toResponse(StudentGroup entity) {
        StudentGroupResponse response = new StudentGroupResponse();
        response.setId(entity.getId());
        response.setGroupName(entity.getGroupName());
        response.setStatus(entity.isStatus());
        response.setRoles(entity.getRoles());

        if (entity.getClazz() != null) {
            response.setClassId(entity.getClazz().getId());
            response.setClassName(entity.getClazz().getClassCode());
        }
        if (entity.getAccount() != null) {
            response.setAccountId(entity.getAccount().getId());
            response.setStudentName(entity.getAccount().getFullName());
            response.setStudentEmail(entity.getAccount().getEmail());
        }
        return response;
    }
}
