package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Response.RegisterResponse;

public class ResponseRegisterMapper {

    public static RegisterResponse toResponse(Account request){
        RegisterResponse response = new RegisterResponse();
        response.setEmail(request.getEmail());
        response.setFullName(request.getFullName());
        response.setPhoneNumber(request.getPhone());
        response.setStudentCode(request.getStudentCode());
        response.setLecturerCode(request.getLecturerCode());
        response.setRoles(request.getRole().getName());
        response.setActive(request.getIsActive());
        return response;
    }
}
