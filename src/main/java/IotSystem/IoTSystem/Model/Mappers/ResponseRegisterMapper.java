package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Response.RegisterResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;

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
