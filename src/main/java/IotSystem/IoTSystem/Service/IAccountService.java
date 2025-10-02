package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAccountService {
    String login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);

    // Profile

    ProfileResponse updateProfile(UpdateAccountRequest request);
 // xem thong tin ca nhan
    ProfileResponse getProfile();

    Page<ProfileResponse> getAllAccounts(Pageable pageable);
    ProfileResponse getAccountById(UUID accountId);


}
