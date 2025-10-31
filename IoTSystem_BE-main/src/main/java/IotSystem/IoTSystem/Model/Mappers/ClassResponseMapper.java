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
        response.setTeacherEmail(classes.getAccount().getEmail());
        response.setTeacherName(classes.getAccount().getFullName());
        response.setCreatedAt(classes.getCreatedAt());
        response.setUpdatedAt(classes.getUpdatedAt());

        return response;
    }
}
