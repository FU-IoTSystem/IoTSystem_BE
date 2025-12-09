package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Request.AccountRequest;
import IotSystem.IoTSystem.Model.Request.ChangePasswordRequest;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import IotSystem.IoTSystem.Model.Response.RegisterResponse;
import IotSystem.IoTSystem.Model.Response.StudentExportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAccountService {
    String login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    RegisterResponse updating(RegisterRequest registerRequest, UUID id);
    // Profile

    ProfileResponse updateProfile(UpdateAccountRequest request);
    String changePassword(ChangePasswordRequest request);
    // xem thong tin ca nhan
    ProfileResponse getProfile();

    List<ProfileResponse> getAllAccounts();
    ProfileResponse getAccountById(UUID accountId);
    List<ProfileResponse> getAllbyRoleLecture();
    List<ProfileResponse> getAllStudent();
    List<ProfileResponse> getStudentsByClassCode(String classCode);
    List<ProfileResponse> getStudentsByClassId(UUID classId);

    // Export student data for Excel
    List<StudentExportResponse> exportStudents();

    ProfileResponse createAStudent(RegisterRequest request);

    ProfileResponse createALecturer(RegisterRequest request);

    void deleteAccount(UUID accountId);

}
