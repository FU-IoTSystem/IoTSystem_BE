package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;

import java.util.UUID;

public interface IAccountService {
    String login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);

    // Profile

    ProfileResponse updateProfile(UpdateAccountRequest request);

}
