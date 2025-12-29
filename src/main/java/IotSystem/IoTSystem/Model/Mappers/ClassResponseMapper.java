package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Response.ClassResponse;

import java.util.UUID;

public class ClassResponseMapper {
    public static ClassResponse toResponse(Classes classes, UUID teacherID){
        ClassResponse response = new ClassResponse();

        response.setClassCode(classes.getClassCode());
        response.setId(classes.getId());
        response.setStatus(classes.isStatus());
        response.setSemester(classes.getSemester());
        response.setTeacherId(teacherID);

        // Handle null account (when lecturer is deleted, account becomes null)
        if (classes.getAccount() != null) {
            response.setTeacherEmail(classes.getAccount().getEmail());
            response.setTeacherName(classes.getAccount().getFullName());
        } else {
            response.setTeacherEmail(null);
            response.setTeacherName("N/A");
        }

        response.setCreatedAt(classes.getCreatedAt());
        response.setUpdatedAt(classes.getUpdatedAt());

        return response;
    }
}
