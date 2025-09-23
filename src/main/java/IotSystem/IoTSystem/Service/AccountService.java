package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;

public interface AccountService {
    String login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);
}
