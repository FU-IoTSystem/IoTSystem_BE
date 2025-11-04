package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String studentCode;
    private String roles;
    private boolean isActive;
}
