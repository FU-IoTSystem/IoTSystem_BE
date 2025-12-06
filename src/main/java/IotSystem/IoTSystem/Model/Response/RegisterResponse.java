package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Roles;
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
    private String lecturerCode;
    private String roles;
    private boolean isActive;
}
